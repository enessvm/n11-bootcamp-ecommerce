package com.n11.bootcamp.ecommerce.events.stock;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Reply event published by stock-service when a {@link ReserveStockCommand} cannot be satisfied.
 *
 * <p>Routing key: {@code stock.reservation-failed}. Exchange: {@code stock.events}.
 */

public record StockReservationFailed(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Reason reason,
        long failedProductId
) {

    public enum Reason {
        INSUFFICIENT_STOCK,
        STOCK_NOT_FOUND
    }
}