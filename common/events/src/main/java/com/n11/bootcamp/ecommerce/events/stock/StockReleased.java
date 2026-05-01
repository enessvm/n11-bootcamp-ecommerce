package com.n11.bootcamp.ecommerce.events.stock;

import java.time.Instant;
import java.util.UUID;

/**
 * Reply event published by stock-service after a successful
 * {@link ReleaseStockCommand}. Confirms reservations are released and
 * stock returned to inventory.
 *
 * <p>Routing key: {@code stock.released}. Exchange: {@code stock.events}.
 */
public record StockReleased(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId
) {}