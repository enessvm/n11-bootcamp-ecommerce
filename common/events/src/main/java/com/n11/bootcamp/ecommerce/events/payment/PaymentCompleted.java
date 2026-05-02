package com.n11.bootcamp.ecommerce.events.payment;

import java.time.Instant;
import java.util.UUID;

/**
 * Reply event published by payment-service after the user successfully
 * completes authentication and the provider confirms the charge.
 *
 * <p>Triggered by the callback endpoint resolving the provider's session
 * with a successful status.
 *
 * <p>Routing key: {@code payment.completed}. Exchange: {@code payment.events}.
 */

public record PaymentCompleted(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Long paymentAttemptId,
        String providerPaymentId
) {}