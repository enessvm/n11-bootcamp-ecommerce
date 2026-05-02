package com.n11.bootcamp.ecommerce.events.payment;

import java.time.Instant;
import java.util.UUID;

/**
 * Reply event published by payment-service after a successful provider
 * session initialization. Carries the URL the user must visit to complete
 * authentication.
 *
 * <p>Order-service stores {@code paymentPageUrl} on the order; the frontend
 * reads it via {@code GET /orders/{id}} and redirects the user to the
 * provider's hosted checkout page.
 *
 * <p>Saga state remains in {@code PAYMENT_INITIATED} until the user
 * completes (or abandons) authentication. Final {@link PaymentCompleted}
 * or {@link PaymentFailed} arrives via payment-service's callback handler.
 *
 * <p>Routing key: {@code payment.initiated}. Exchange: {@code payment.events}.
 */
public record PaymentInitiated(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Long paymentAttemptId,
        String paymentPageUrl
) {}