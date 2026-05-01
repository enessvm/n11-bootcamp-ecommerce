package com.n11.bootcamp.ecommerce.order.service;

import com.n11.bootcamp.ecommerce.events.stock.ReserveStockCommand;
import com.n11.bootcamp.ecommerce.events.stock.StockReservationFailed;
import com.n11.bootcamp.ecommerce.events.stock.StockReserved;
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
                        .map(this::toCommandItem)
                        .toList()
        );

        eventPublisher.publishReserveStock(command);
        log.info("Saga started orderId={} sagaId={}", order.getId(), order.getSagaId());
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


        order.setSagaState(SagaState.COMPLETED);
        log.info("Saga completed orderId={} sagaId={} (slice 1a auto-completion)",
                order.getId(), order.getSagaId());
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
        order.setFailureReason(buildFailureReason(event));
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

    private ReserveStockCommand.Item toCommandItem(OrderLineItem line) {
        return new ReserveStockCommand.Item(line.getProductId(), line.getQuantity());
    }

    private String buildFailureReason(StockReservationFailed event) {
        return event.reason() == StockReservationFailed.Reason.INSUFFICIENT_STOCK
                ? "Insufficient stock for one or more items"
                : "Stock not found for one or more items";
    }
}