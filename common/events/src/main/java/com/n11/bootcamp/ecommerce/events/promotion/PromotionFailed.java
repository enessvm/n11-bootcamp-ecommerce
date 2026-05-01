package com.n11.bootcamp.ecommerce.events.promotion;

import java.time.Instant;
import java.util.UUID;

/**
 * Reply event published by promotion-service when an
 * {@link ApplyPromotionCommand} fails.
 *
 * <p>Routing key: {@code promotion.failed}.
 * Exchange: {@code promotion.events}.
 */
public record PromotionFailed(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Reason reason,
        String message
) {

    public enum Reason {
        NOT_FOUND,
        INACTIVE,
        NOT_YET_VALID,
        EXPIRED,
        MAX_USES_REACHED,
        CART_BELOW_MINIMUM
    }
}