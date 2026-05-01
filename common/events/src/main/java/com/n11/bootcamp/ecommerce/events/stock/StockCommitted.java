package com.n11.bootcamp.ecommerce.events.stock;

import java.time.Instant;
import java.util.UUID;

/**
 * Reply event published by stock-service after a successful
 * {@link CommitStockCommand}. Confirms reservations are committed and
 * stock is permanently consumed.
 *
 * <p>Routing key: {@code stock.committed}. Exchange: {@code stock.events}.
 */
public record StockCommitted(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId
) {}