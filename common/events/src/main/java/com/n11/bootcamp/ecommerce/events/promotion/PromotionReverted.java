package com.n11.bootcamp.ecommerce.events.promotion;

import java.time.Instant;
import java.util.UUID;

/**
 * Reply event from promotion-service confirming a promotion redemption
 * was reverted: the {@code promotion_redemption} row was deleted and
 * {@code Promotion.timesRedeemed} was atomically decremented.
 *
 * <p>Routing key: {@code promotion.reverted}.
 * Exchange: {@code promotion.events}.
 */
public record PromotionReverted(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Long promotionId,
        String code
) {}
