package com.n11.bootcamp.ecommerce.events.stock;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Reply event published by stock-service after a successful
 * {@link ReserveStockCommand}. Carries reservation references that
 * order-service uses for later commit/release commands.
 *
 * <p>Routing key: {@code stock.reserved}. Exchange: {@code stock.events}.
 */

public record StockReserved(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        List<Reservation> reservations,
        Instant expiresAt
) {

    public record Reservation(
            long reservationId,
            long productId,
            int quantity
    ) {}
}