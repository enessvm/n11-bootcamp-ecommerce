package com.n11.bootcamp.ecommerce.payment.service.impl;

import com.n11.bootcamp.ecommerce.events.payment.ChargePaymentCommand;
import com.n11.bootcamp.ecommerce.events.payment.PaymentFailed;
import com.n11.bootcamp.ecommerce.payment.entity.Money;
import com.n11.bootcamp.ecommerce.payment.entity.PaymentAttempt;
import com.n11.bootcamp.ecommerce.payment.entity.PaymentStatus;
import com.n11.bootcamp.ecommerce.payment.event.PaymentEventPublisher;
import com.n11.bootcamp.ecommerce.payment.exception.UnsupportedPaymentProviderException;
import com.n11.bootcamp.ecommerce.payment.gateway.PaymentGateway;
import com.n11.bootcamp.ecommerce.payment.repository.PaymentAttemptRepository;
import com.n11.bootcamp.ecommerce.payment.service.PaymentService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentAttemptRepository attemptRepository;
    private final PaymentEventPublisher eventPublisher;
    private final Map<String, PaymentGateway> gatewaysByName;

    public PaymentServiceImpl(PaymentAttemptRepository attemptRepository,
                              PaymentEventPublisher eventPublisher,
                              List<PaymentGateway> gateways) {
        this.attemptRepository = attemptRepository;
        this.eventPublisher = eventPublisher;
        this.gatewaysByName = gateways.stream()
                .collect(Collectors.toMap(PaymentGateway::getProviderName, Function.identity()));
    }

    @PostConstruct
    void logRegisteredGateways() {
        log.info("Registered payment gateways: {}", gatewaysByName.keySet());
    }

    // ---- Saga  consumer ----
    @Override
    @Transactional
    public void consumeChargeCommand(ChargePaymentCommand command) {
        var existing = attemptRepository.findBySagaId(command.sagaId()).orElse(null);
        if (existing != null) {
            replayPriorOutcome(existing);
            return;
        }

        // Initialize provider session.
        var gateway = resolveGateway(command.provider());
        var result = gateway.initiate(command);

        if (!result.success()) {

            log.warn("Provider initialize failed sagaId={} provider={} message={}",
                    command.sagaId(), command.provider(), result.errorMessage());
            eventPublisher.publishPaymentFailed(
                    command.sagaId(),
                    -1L,  // No attemptId yet — never persisted.
                    PaymentFailed.Reason.INITIALIZE_FAILED,
                    result.errorMessage()
            );
            return;
        }

        // Persist INITIATED attempt with the provider's token + URL.
        var attempt = PaymentAttempt.create(
                command.sagaId(),
                command.orderId(),
                command.provider(),
                new Money(command.amount(), command.currency()),
                result.providerToken(),
                result.paymentPageUrl()
        );
        attempt = attemptRepository.save(attempt);

        eventPublisher.publishPaymentInitiated(
                command.sagaId(),
                attempt.getId(),
                result.paymentPageUrl()
        );
        log.info("Initiated payment sagaId={} provider={} paymentAttemptId={}",
                command.sagaId(), command.provider(), attempt.getId());
    }

    @Override
    @Transactional
    public void handleCallback(String providerName, String providerToken) {
        var attempt = attemptRepository.findByCheckoutToken(providerToken).orElse(null);

        if (attempt == null) {
            // Provider sent a callback for a token we don't know.
            log.error("Callback received for unknown token provider={} token={}",
                    providerName, providerToken);
            throw new IllegalStateException("Unknown checkoutToken: " + providerToken);
        }

        // already terminal = no-op (provider may retry callbacks).
        if (attempt.getStatus() != PaymentStatus.INITIATED) {
            log.info("Callback ignored for already-terminal attempt id={} status={}",
                    attempt.getId(), attempt.getStatus());
            return;
        }

        var gateway = resolveGateway(providerName);
        var result = gateway.resolve(providerToken);

        if (result.success()) {
            attempt.markSucceeded(result.providerPaymentId());
            eventPublisher.publishPaymentCompleted(
                    attempt.getSagaId(),
                    attempt.getId(),
                    result.providerPaymentId()
            );
            log.info("Payment completed sagaId={} paymentAttemptId={} providerPaymentId={}",
                    attempt.getSagaId(), attempt.getId(), result.providerPaymentId());
        } else {
            attempt.markFailed(result.errorMessage());
            eventPublisher.publishPaymentFailed(
                    attempt.getSagaId(),
                    attempt.getId(),
                    PaymentFailed.Reason.COMPLETION_FAILED,
                    result.errorMessage()
            );
            log.info("Payment failed sagaId={} paymentAttemptId={} message={}",
                    attempt.getSagaId(), attempt.getId(), result.errorMessage());
        }
    }

    // ---- Helpers ----

    /**
     * Redelivery handling. The attempt already exists; republish whatever
     * reply matches its current state.
     */
    private void replayPriorOutcome(PaymentAttempt existing) {
        log.info("Replaying outcome for sagaId={} status={}",
                existing.getSagaId(), existing.getStatus());

        switch (existing.getStatus()) {
            case INITIATED -> eventPublisher.publishPaymentInitiated(
                    existing.getSagaId(), existing.getId(), existing.getPaymentPageUrl());
            case SUCCEEDED -> eventPublisher.publishPaymentCompleted(
                    existing.getSagaId(), existing.getId(), existing.getProviderPaymentId());
            case FAILED -> eventPublisher.publishPaymentFailed(
                    existing.getSagaId(),
                    existing.getId(),
                    PaymentFailed.Reason.COMPLETION_FAILED,
                    existing.getFailureReason());
        }
    }

    private PaymentGateway resolveGateway(String name) {
        var gateway = gatewaysByName.get(name);
        if (gateway == null) {
            throw new UnsupportedPaymentProviderException(name);
        }
        return gateway;
    }
}