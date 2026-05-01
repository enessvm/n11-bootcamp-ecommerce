package com.n11.bootcamp.ecommerce.events.promotion;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Command from order-service's saga orchestrator to promotion-service.
 *
 * <p>Routing key: {@code promotion.commands.apply}.
 * Exchange: {@code promotion.commands}.
 *
 * <p>Reply events: {@link PromotionApplied} on success,
 * {@link PromotionFailed} on validation failure or contention.
 */
public record ApplyPromotionCommand(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Long orderId,
        String code,
        BigDecimal cartTotal,
        String currency
) {}