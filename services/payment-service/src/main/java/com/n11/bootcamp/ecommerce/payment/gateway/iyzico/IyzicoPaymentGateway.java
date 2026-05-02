package com.n11.bootcamp.ecommerce.payment.gateway.iyzico;

import com.iyzipay.Options;
import com.iyzipay.model.CheckoutForm;
import com.iyzipay.model.CheckoutFormInitialize;
import com.iyzipay.model.Status;
import com.iyzipay.request.RetrieveCheckoutFormRequest;
import com.n11.bootcamp.ecommerce.events.payment.ChargePaymentCommand;
import com.n11.bootcamp.ecommerce.payment.config.IyzicoProperties;
import com.n11.bootcamp.ecommerce.payment.gateway.InitiateResult;
import com.n11.bootcamp.ecommerce.payment.gateway.PaymentGateway;
import com.n11.bootcamp.ecommerce.payment.gateway.ResolveResult;
import com.n11.bootcamp.ecommerce.payment.mapper.IyzicoRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Iyzico-specific implementation of {@link PaymentGateway}. Wraps the
 * Iyzico SDK; converts between provider types and the platform's
 * generic result records.
 *
 * <p>Adding another provider (Stripe, PayPal) means adding a sibling
 * class — same pattern, no changes here.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IyzicoPaymentGateway implements PaymentGateway {

    public static final String PROVIDER_NAME = "iyzico";

    private final Options options;
    private final IyzicoRequestMapper requestMapper;
    private final IyzicoProperties properties;

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public InitiateResult initiate(ChargePaymentCommand command) {
        var request = requestMapper.toInitializeRequest(command, properties.getCallbackUrl());
        var response = CheckoutFormInitialize.create(request, options);

        if (Status.SUCCESS.getValue().equals(response.getStatus())) {
            log.info("Iyzico initialize succeeded sagaId={} token={}",
                    command.sagaId(), response.getToken());
            return InitiateResult.success(response.getToken(), response.getPaymentPageUrl());
        }

        log.warn("Iyzico initialize failed sagaId={} errorCode={} errorMessage={}",
                command.sagaId(), response.getErrorCode(), response.getErrorMessage());
        return InitiateResult.failure(response.getErrorMessage());
    }

    @Override
    public ResolveResult resolve(String providerToken) {
        var request = new RetrieveCheckoutFormRequest();
        request.setToken(providerToken);

        var response = CheckoutForm.retrieve(request, options);

        // Two failure modes: API call itself failed, OR call succeeded but
        // the bank declined. Both surface as ResolveResult.failure.
        if (Status.SUCCESS.getValue().equals(response.getStatus())
                && "SUCCESS".equals(response.getPaymentStatus())) {
            log.info("Iyzico retrieve succeeded token={} paymentId={}",
                    providerToken, response.getPaymentId());
            return ResolveResult.success(response.getPaymentId());
        }

        var errorMessage = response.getErrorMessage() != null
                ? response.getErrorMessage()
                : "Payment status: " + response.getPaymentStatus();
        log.warn("Iyzico retrieve failed token={} errorMessage={}",
                providerToken, errorMessage);
        return ResolveResult.failure(errorMessage);
    }
}