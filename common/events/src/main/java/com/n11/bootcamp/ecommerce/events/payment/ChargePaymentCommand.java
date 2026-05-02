package com.n11.bootcamp.ecommerce.events.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Command from order-service's saga orchestrator to payment-service.
 * Initiates a hosted-checkout payment via the configured payment provider.
 *
 * <p>Routing key: {@code payment.commands.charge}.
 * Exchange: {@code payment.commands}.
 *
 * <p>Reply events:
 * <ul>
 *   <li>{@link PaymentInitiated} — provider session created, user must
 *       complete authentication at the returned {@code paymentPageUrl}.</li>
 *   <li>{@link PaymentCompleted} — final success after the user finishes
 *       authentication and the provider confirms the charge.</li>
 *   <li>{@link PaymentFailed} — initialization or completion failure.</li>
 * </ul>
 */
public record ChargePaymentCommand(
        UUID eventId,
        Instant occurredAt,
        UUID sagaId,
        Long orderId,
        String provider,
        BigDecimal amount, // total after sun
        BigDecimal paidAmount, // total after discount
        String currency,
        Customer customer,
        Address shippingAddress,
        Address billingAddress,
        List<LineItem> lineItems
) {

    /**
     * Snapshot of the buyer at order time.
     */
    public record Customer(
            String id,
            String name,
            String surname,
            String email,
            String phoneNumber,
            String identityNumber,
            String ipAddress
    ) {}

    public record Address(
            String contactName,
            String addressLine,
            String city,
            String country,
            String zipCode
    ) {}

    public record LineItem(
            String id,
            String name,
            String category,
            BigDecimal price,
            int quantity
    ) {}
}