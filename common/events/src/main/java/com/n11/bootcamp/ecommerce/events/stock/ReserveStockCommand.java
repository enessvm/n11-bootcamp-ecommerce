package com.n11.bootcamp.ecommerce.events.stock;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Command from order-service's saga orchestrator to stock-service.
 * Requests reservation of stock for a list of items as part of an order saga.
 *
 * <p>Routing key: {@code stock.commands.reserve}. Exchange: {@code stock.commands}.
 *
 * <p>Reply events: {@code StockReserved} on success, {@code StockReservationFailed}
 * on insufficient stock or missing products. Both routed to {@code stock.events}
 * exchange and consumed by order-service.
 */

public record ReserveStockCommand(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Long orderId,
        List<Item> items
) {

    public record Item(
            long productId,
            int quantity
    ) {}
}