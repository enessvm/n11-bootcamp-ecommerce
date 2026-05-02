package com.n11.bootcamp.ecommerce.payment.service;

import com.n11.bootcamp.ecommerce.events.payment.ChargePaymentCommand;

public interface PaymentService {

    void consumeChargeCommand(ChargePaymentCommand command);

    void handleCallback(String providerName, String providerToken);
}