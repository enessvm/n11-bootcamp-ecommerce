package com.n11.bootcamp.ecommerce.stock.event;

import com.n11.bootcamp.ecommerce.events.RoutingKeys;
import com.n11.bootcamp.ecommerce.events.stock.*;
import com.n11.bootcamp.ecommerce.stock.entity.StockReservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Publishes saga reply events to {@code stock.events}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishStockReserved(UUID sagaId,
                                     List<StockReservation> reservations,
                                     Instant expiresAt) {
        List<StockReserved.Reservation> payload = reservations.stream()
                .map(r -> new StockReserved.Reservation(r.getId(), r.getProductId(), r.getQuantity()))
                .toList();

        StockReserved event = new StockReserved(
                UUID.randomUUID(),
                Instant.now(),
                sagaId,
                payload,
                expiresAt
        );
        send(RoutingKeys.STOCK_RESERVED, event);
        log.info("Published StockReserved sagaId={} reservations={}", sagaId, payload.size());
    }

    public void publishReservationFailed(UUID sagaId,
                                         StockReservationFailed.Reason reason,
                                         long failedProductId) {
        StockReservationFailed event = new StockReservationFailed(
                UUID.randomUUID(),
                Instant.now(),
                sagaId,
                reason,
                failedProductId
        );
        send(RoutingKeys.STOCK_RESERVATION_FAILED, event);
        log.info("Published StockReservationFailed sagaId={} reason={} productId={}",
                sagaId, reason, failedProductId);
    }

    public void publishStockCommitted(UUID sagaId) {
        StockCommitted event = new StockCommitted(
                UUID.randomUUID(),
                Instant.now(),
                sagaId
        );
        send(RoutingKeys.STOCK_COMMITTED, event);
        log.info("Published StockCommitted sagaId={}", sagaId);
    }

    public void publishCommitFailed(UUID sagaId) {
        StockCommitFailed event = new StockCommitFailed(
                UUID.randomUUID(),
                Instant.now(),
                sagaId
        );
        send(RoutingKeys.STOCK_COMMIT_FAILED, event);
        log.info("Published StockCommitFailed sagaId={}", sagaId);
    }

    public void publishStockReleased(UUID sagaId) {
        StockReleased event = new StockReleased(
                UUID.randomUUID(),
                Instant.now(),
                sagaId
        );
        send(RoutingKeys.STOCK_RELEASED, event);
        log.info("Published StockReleased sagaId={}", sagaId);
    }

    private void send(String routingKey, Object payload) {
        rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE_STOCK_EVENTS, routingKey, payload);
    }
}