package com.n11.bootcamp.ecommerce.events.stock;

import java.time.Instant;
import java.util.UUID;

/**
 * Command from order-service for compensation flows (saga step failed after stock reservation).
 *
 * <p>Routing key: {@code stock.commands.release}. Exchange: {@code stock.commands}.
 */

public record ReleaseStockCommand(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Long orderId
) {}