package com.n11.bootcamp.ecommerce.payment.gateway;

/**
 * <p>{@code success=true} → {@code providerPaymentId} populated.
 * {@code success=false} → {@code errorMessage} populated.
 */
public record ResolveResult(
        boolean success,
        String providerPaymentId,
        String errorMessage
) {

    public static ResolveResult success(String providerPaymentId) {
        return new ResolveResult(true, providerPaymentId, null);
    }

    public static ResolveResult failure(String errorMessage) {
        return new ResolveResult(false, null, errorMessage);
    }
}