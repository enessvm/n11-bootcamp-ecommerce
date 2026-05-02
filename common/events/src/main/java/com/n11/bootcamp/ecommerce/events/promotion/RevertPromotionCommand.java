package com.n11.bootcamp.ecommerce.events.promotion;

import java.time.Instant;
import java.util.UUID;

/**
 * Command from order-service's saga orchestrator to promotion-service to
 * compensate a previously-applied promotion. Carries only the sagaId —
 * promotion-service uses its own {@code promotion_redemption.saga_id} row
 * as the source of truth for which promotion to decrement.
 *
 * <p>Routing key: {@code promotion.commands.revert}.
 * Exchange: {@code promotion.commands}.
 *
 * <p>Reply event: {@link PromotionReverted} on success. Anomalies
 * (missing redemption row, counter already 0) throw to DLQ — there is
 * no PromotionRevertFailed event.
 */
public record RevertPromotionCommand(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId
) {}
