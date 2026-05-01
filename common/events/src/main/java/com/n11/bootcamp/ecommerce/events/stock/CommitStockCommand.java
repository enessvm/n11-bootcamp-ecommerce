package com.n11.bootcamp.ecommerce.events.stock;

import java.time.Instant;
import java.util.UUID;

/**
 * Command from order-service after payment success.
 *
 * <p>Routing key: {@code stock.commands.commit}. Exchange: {@code stock.commands}.
 */

public record CommitStockCommand(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Long orderId
) {}