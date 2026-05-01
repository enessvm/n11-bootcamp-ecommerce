package com.n11.bootcamp.ecommerce.events.stock;

import java.time.Instant;
import java.util.UUID;

/**
 * Reply event when a {@link CommitStockCommand} cannot be honored.
 *
 * <p>Routing key: {@code stock.commit-failed}. Exchange: {@code stock.events}.
 */
public record StockCommitFailed(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId
) {}