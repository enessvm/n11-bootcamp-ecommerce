package com.n11.bootcamp.ecommerce.payment.exception;

import com.n11.bootcamp.ecommerce.web.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a saga command references a {@code provider} name that has
 * no registered {@link com.n11.bootcamp.ecommerce.payment.gateway.PaymentGateway}.
 */
public class UnsupportedPaymentProviderException extends BusinessException {

    public UnsupportedPaymentProviderException(String providerName) {
        super("UNSUPPORTED_PAYMENT_PROVIDER", HttpStatus.UNPROCESSABLE_ENTITY,
                "Unsupported payment provider: " + providerName);
    }
}