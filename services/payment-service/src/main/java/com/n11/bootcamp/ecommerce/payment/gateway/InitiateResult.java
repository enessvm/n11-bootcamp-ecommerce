package com.n11.bootcamp.ecommerce.payment.gateway;

/**
 * <p>{@code success=true} → {@code providerToken} and {@code paymentPageUrl} populated.
 * {@code success=false} → {@code errorMessage} populated.
 */
public record InitiateResult(
        boolean success,
        String providerToken,
        String paymentPageUrl,
        String errorMessage
) {

    public static InitiateResult success(String providerToken, String paymentPageUrl) {
        return new InitiateResult(true, providerToken, paymentPageUrl, null);
    }

    public static InitiateResult failure(String errorMessage) {
        return new InitiateResult(false, null, null, errorMessage);
    }
}