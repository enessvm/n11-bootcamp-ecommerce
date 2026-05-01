package com.n11.bootcamp.ecommerce.order.service.impl;

import com.n11.bootcamp.ecommerce.events.promotion.ApplyPromotionCommand;
import com.n11.bootcamp.ecommerce.events.promotion.PromotionFailed;
import com.n11.bootcamp.ecommerce.events.promotion.PromotionApplied;
import com.n11.bootcamp.ecommerce.events.stock.*;
import com.n11.bootcamp.ecommerce.order.entity.Money;
import com.n11.bootcamp.ecommerce.order.entity.Order;
import com.n11.bootcamp.ecommerce.order.entity.OrderLineItem;
import com.n11.bootcamp.ecommerce.order.entity.SagaState;
import com.n11.bootcamp.ecommerce.order.event.SagaEventPublisher;
import com.n11.bootcamp.ecommerce.order.repository.OrderRepository;
import com.n11.bootcamp.ecommerce.order.service.SagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaServiceImpl implements SagaService {

    private final OrderRepository orderRepository;
    private final SagaEventPublisher eventPublisher;

    @Override
    @Transactional
    public void start(Order order) {
        ReserveStockCommand command = new ReserveStockCommand(
                UUID.randomUUID(),
                Instant.now(),
                order.getSagaId(),
                order.getId(),
                order.getLineItems().stream()
                        .map(this::toReserveStockItem)
                        .toList()
        );

        eventPublisher.publishReserveStock(command);
        log.info("Saga started orderId={} sagaId={}",
                order.getId(), order.getSagaId());
    }

    @Override
    @Transactional
    public void onStockReserved(StockReserved event) {
        Order order = loadBySagaId(event.sagaId());

        if (order.getSagaState() != SagaState.INITIATED) {
            log.warn("Ignoring StockReserved sagaId={} — order in unexpected state {}",
                    event.sagaId(), order.getSagaState());
            return;
        }

        order.setSagaState(SagaState.STOCK_RESERVED);
        log.info("Saga advanced orderId={} sagaId={} state=STOCK_RESERVED",
                order.getId(), order.getSagaId());

        if (hasCoupon(order)) {
            ApplyPromotionCommand command = new ApplyPromotionCommand(
                    UUID.randomUUID(),
                    Instant.now(),
                    order.getSagaId(),
                    order.getId(),
                    order.getAppliedCouponCode(),
                    order.getSubtotal().getAmount(),
                    order.getSubtotal().getCurrency()
            );
            eventPublisher.publishApplyPromotion(command);
            // State stays STOCK_RESERVED until promotion reply lands.
        } else {
            // No coupon — skip promotion, go straight to commit.
            publishCommitAndAdvance(order);
        }
    }

    @Override
    @Transactional
    public void onStockReservationFailed(StockReservationFailed event) {
        Order order = loadBySagaId(event.sagaId());

        if (order.getSagaState() != SagaState.INITIATED) {
            log.warn("Ignoring StockReservationFailed sagaId={} — order in unexpected state {}",
                    event.sagaId(), order.getSagaState());
            return;
        }

        order.setSagaState(SagaState.FAILED);
        order.setFailureReason(buildStockFailureReason(event));
        log.info("Saga failed orderId={} sagaId={} reason={}",
                order.getId(), order.getSagaId(), order.getFailureReason());
    }

    @Override
    @Transactional
    public void onPromotionApplied(PromotionApplied event) {
        Order order = loadBySagaId(event.sagaId());

        if (order.getSagaState() != SagaState.STOCK_RESERVED) {
            log.warn("Ignoring PromotionApplied sagaId={} — order in unexpected state {}",
                    event.sagaId(), order.getSagaState());
            return;
        }

        // Apply discount to order. Subtotal stays as the pre-discount amount;
        // total = subtotal - cartTotalDiscount.
        Money currentSubtotal = order.getSubtotal();
        Money discount = new Money(event.cartDiscountAmount(), event.currency());
        Money newTotal = new Money(
                currentSubtotal.getAmount().subtract(event.cartDiscountAmount()),
                event.currency()
        );

        order.setCartTotalDiscount(discount);
        order.setTotal(newTotal);
        order.setSagaState(SagaState.PROMOTION_APPLIED);
        log.info("Saga advanced orderId={} sagaId={} state=PROMOTION_APPLIED discount={} {}",
                order.getId(), order.getSagaId(),
                event.cartDiscountAmount(), event.currency());


        // Promotion done — commit stock.
        publishCommitAndAdvance(order);
    }

    @Override
    @Transactional
    public void onPromotionApplicationFailed(PromotionFailed event) {
        Order order = loadBySagaId(event.sagaId());

        if (order.getSagaState() != SagaState.STOCK_RESERVED) {
            log.warn("Ignoring PromotionApplicationFailed sagaId={} — order in unexpected state {}",
                    event.sagaId(), order.getSagaState());
            return;
        }

        order.setSagaState(SagaState.PROMOTION_FAILED);
        order.setFailureReason("Promotion " + event.reason() + ": " + event.message());
        log.info("Saga failed orderId={} sagaId={} reason={}",
                order.getId(), order.getSagaId(), order.getFailureReason());

        // Compensate: release the stock reservations.
        publishReleaseAndAdvance(order);
    }

    @Override
    @Transactional
    public void onStockCommitted(StockCommitted event) {
        Order order = loadBySagaId(event.sagaId());

        if (order.getSagaState() != SagaState.COMMIT_REQUESTED) {
            log.warn("Ignoring StockCommitted sagaId={} — order in unexpected state {}",
                    event.sagaId(), order.getSagaState());
            return;
        }

        order.setSagaState(SagaState.COMPLETED);
        log.info("Saga completed orderId={} sagaId={}",
                order.getId(), order.getSagaId());
    }

    @Override
    @Transactional
    public void onStockCommitFailed(StockCommitFailed event) {
        Order order = loadBySagaId(event.sagaId());

        if (order.getSagaState() != SagaState.COMMIT_REQUESTED) {
            log.warn("Ignoring StockCommitFailed sagaId={} — order in unexpected state {}",
                    event.sagaId(), order.getSagaState());
            return;
        }

        // Anomaly — past compensation point. Reservations not found by stock-service
        // shouldn't happen for a saga that successfully reserved earlier. Surface
        // via failureReason and transition to FAILED.
        order.setFailureReason("Stock commit rejected: reservation not found in stock-service");
        log.error("Saga failed at commit orderId={} sagaId={} — anomaly: reservations missing",
                order.getId(), order.getSagaId());
        order.setSagaState(SagaState.FAILED);
    }

    @Override
    @Transactional
    public void onStockReleased(StockReleased event) {
        Order order = loadBySagaId(event.sagaId());

        if (order.getSagaState() != SagaState.COMPENSATING_STOCK) {
            log.warn("Ignoring StockReleased sagaId={} — order in unexpected state {}",
                    event.sagaId(), order.getSagaState());
            return;
        }

        // Compensation done
        order.setSagaState(SagaState.FAILED);
        log.info("Saga compensation complete orderId={} sagaId={} state=FAILED",
                order.getId(), order.getSagaId());
    }

    // ---- Helpers ----
    private void publishCommitAndAdvance(Order order) {
        order.setSagaState(SagaState.COMMIT_REQUESTED);
        CommitStockCommand command = new CommitStockCommand(
                UUID.randomUUID(),
                Instant.now(),
                order.getSagaId(),
                order.getId()
        );
        eventPublisher.publishCommitStock(command);
        log.info("Saga advanced orderId={} sagaId={} state=COMMIT_REQUESTED",
                order.getId(), order.getSagaId());
    }

    private void publishReleaseAndAdvance(Order order) {
        order.setSagaState(SagaState.COMPENSATING_STOCK);
        ReleaseStockCommand command = new ReleaseStockCommand(
                UUID.randomUUID(),
                Instant.now(),
                order.getSagaId(),
                order.getId()
        );
        eventPublisher.publishReleaseStock(command);
        log.info("Saga advanced orderId={} sagaId={} state=COMPENSATING_STOCK",
                order.getId(), order.getSagaId());
    }

    private Order loadBySagaId(UUID sagaId) {
        return orderRepository.findBySagaId(sagaId)
                .orElseThrow(() -> new IllegalStateException(
                        "Saga reply received for unknown sagaId: " + sagaId));
    }

    private ReserveStockCommand.Item toReserveStockItem(OrderLineItem line) {
        return new ReserveStockCommand.Item(line.getProductId(), line.getQuantity());
    }

    private boolean hasCoupon(Order order) {
        String code = order.getAppliedCouponCode();
        return code != null && !code.isBlank();
    }

    private String buildStockFailureReason(StockReservationFailed event) {
        return event.reason() == StockReservationFailed.Reason.INSUFFICIENT_STOCK
                ? "Insufficient stock for one or more items"
                : "Stock not found for one or more items";
    }

}