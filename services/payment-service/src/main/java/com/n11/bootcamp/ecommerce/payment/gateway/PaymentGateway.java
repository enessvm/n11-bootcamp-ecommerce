package com.n11.bootcamp.ecommerce.payment.gateway;

import com.n11.bootcamp.ecommerce.events.payment.ChargePaymentCommand;

/**
 * Port for outbound integration with payment providers (Iyzico, Stripe, etc.).
 */
public interface PaymentGateway {

    /**
     * Identifier matching the {@code provider} field on
     * {@link ChargePaymentCommand}. Convention: lowercase service name
     * (e.g. {@code "iyzico"})
     */
    String getProviderName();

    /**
     * Open a hosted-checkout session. Returns the URL the user must visit
     * and the provider-side token used later for callback resolution.
     */
    InitiateResult initiate(ChargePaymentCommand command);

    /**
     * Resolve a session by token after the user completes authentication.
     * Called from the provider's callback handler.
     */
    ResolveResult resolve(String providerToken);
}