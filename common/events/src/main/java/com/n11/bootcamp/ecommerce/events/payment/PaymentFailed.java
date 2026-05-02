package com.n11.bootcamp.ecommerce.events.payment;

import java.time.Instant;
import java.util.UUID;

/**
 * Reply event published by payment-service when payment fails.
 *
 * <p>Two phases can fail:
 * <ul>
 *   <li>{@code INITIALIZE_FAILED} — the provider rejected session
 *       initialization. Common: invalid amount, configuration error,
 *       provider-side validation.</li>
 *   <li>{@code COMPLETION_FAILED} — the user completed authentication
 *       but the bank declined or the card was rejected post-auth.</li>
 * </ul>
 *
 * <p>Routing key: {@code payment.failed}. Exchange: {@code payment.events}.
 */
public record PaymentFailed(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Long paymentAttemptId,
        Reason reason,
        String message
) {

    public enum Reason {
        INITIALIZE_FAILED,
        COMPLETION_FAILED
    }
}