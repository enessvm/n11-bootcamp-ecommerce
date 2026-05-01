package com.n11.bootcamp.ecommerce.order.service;

import com.n11.bootcamp.ecommerce.events.promotion.ApplyPromotionCommand;
import com.n11.bootcamp.ecommerce.events.promotion.PromotionFailed;
import com.n11.bootcamp.ecommerce.events.promotion.PromotionApplied;
import com.n11.bootcamp.ecommerce.events.stock.ReserveStockCommand;
import com.n11.bootcamp.ecommerce.events.stock.StockReservationFailed;
import com.n11.bootcamp.ecommerce.events.stock.StockReserved;
import com.n11.bootcamp.ecommerce.order.entity.Money;
import com.n11.bootcamp.ecommerce.order.entity.Order;
import com.n11.bootcamp.ecommerce.order.entity.OrderLineItem;
import com.n11.bootcamp.ecommerce.order.entity.SagaState;
import com.n11.bootcamp.ecommerce.order.event.SagaEventPublisher;
import com.n11.bootcamp.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaService {

    private final OrderRepository orderRepository;
    private final SagaEventPublisher eventPublisher;

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
            order.setSagaState(SagaState.COMPLETED);
            log.info("Saga completed orderId={} sagaId={} (no coupon)",
                    order.getId(), order.getSagaId());
        }
    }

    @Transactional
    public void onStockReservationFailed(StockReservationFailed event) {
        Order order = loadBySagaId(event.sagaId());

        if (order.getSagaState() != SagaState.INITIATED) {
            log.warn("Ignoring StockReservationFailed sagaId={} — order in unexpected state {}",
                    event.sagaId(), order.getSagaState());
            return;
        }

        order.setSagaState(SagaState.STOCK_FAILED);
        order.setFailureReason(buildStockFailureReason(event));
        log.info("Saga failed orderId={} sagaId={} reason={}",
                order.getId(), order.getSagaId(), order.getFailureReason());

        order.setSagaState(SagaState.FAILED);
    }


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


        order.setSagaState(SagaState.COMPLETED);
        log.info("Saga completed orderId={} sagaId={} (saga finished)",
                order.getId(), order.getSagaId());
    }


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


        order.setSagaState(SagaState.FAILED);
    }

    // ---- Helpers ----

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