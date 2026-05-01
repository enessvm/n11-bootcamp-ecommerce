package com.n11.bootcamp.ecommerce.events.promotion;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Reply event published by promotion-service after a successful
 * {@link ApplyPromotionCommand}. Carries the resulting discount amount
 * that order-service writes onto the order's {@code cartTotalDiscount}.
 *
 * <p>Routing key: {@code promotion.applied}. Exchange: {@code promotion.events}.
 */
public record PromotionApplied(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Long promotionId,
        String code,
        BigDecimal cartDiscountAmount,
        String currency
) {}