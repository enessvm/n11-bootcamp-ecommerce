package com.n11.bootcamp.ecommerce.events.stock;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Reply event published by stock-service when a {@link ReserveStockCommand}
 * cannot be satisfied.
 *
 * <p>Routing key: {@code stock.reservation-failed}. Exchange: {@code stock.events}.
 */

public record StockReservationFailed(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Reason reason,
        List<UnavailableItem> unavailableItems
) {

    public enum Reason {
        INSUFFICIENT_STOCK,
        STOCK_NOT_FOUND
    }

    /**
     * One per offending item. Empty array when {@code reason = STOCK_NOT_FOUND}
     * for items with no {@code stock_level} row at all.
     */
    public record UnavailableItem(
            long productId,
            int requested,
            int available
    ) {}
}