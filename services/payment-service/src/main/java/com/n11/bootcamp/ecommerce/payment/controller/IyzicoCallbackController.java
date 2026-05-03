package com.n11.bootcamp.ecommerce.payment.controller;

import com.n11.bootcamp.ecommerce.payment.gateway.iyzico.IyzicoPaymentGateway;
import com.n11.bootcamp.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Iyzico-specific callback endpoint. Iyzico POSTs form-encoded {@code token}
 * after the user completes authentication.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class IyzicoCallbackController {

    private final PaymentService paymentService;

    @PostMapping(
            value = "/payments/iyzico/callback",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ResponseEntity<Void> onCallback(@RequestParam("token") String token) {
        log.info("Iyzico callback received token={}", token);
        String returnUrl = paymentService.handleCallback(
                IyzicoPaymentGateway.PROVIDER_NAME, token);
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .location(URI.create(returnUrl))
                .build();
    }
}
