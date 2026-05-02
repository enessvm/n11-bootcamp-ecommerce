package com.n11.bootcamp.ecommerce.payment.event;

import com.n11.bootcamp.ecommerce.events.RoutingKeys;
import com.n11.bootcamp.ecommerce.events.payment.PaymentCompleted;
import com.n11.bootcamp.ecommerce.events.payment.PaymentFailed;
import com.n11.bootcamp.ecommerce.events.payment.PaymentInitiated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Publishes saga reply events to {@code payment.events}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishPaymentInitiated(UUID sagaId,
                                        long paymentAttemptId,
                                        String paymentPageUrl) {
        PaymentInitiated event = new PaymentInitiated(
                UUID.randomUUID(),
                Instant.now(),
                sagaId,
                paymentAttemptId,
                paymentPageUrl
        );
        send(RoutingKeys.PAYMENT_INITIATED, event);
        log.info("Published PaymentInitiated sagaId={} paymentAttemptId={}",
                sagaId, paymentAttemptId);
    }

    public void publishPaymentCompleted(UUID sagaId,
                                        long paymentAttemptId,
                                        String providerPaymentId) {
        PaymentCompleted event = new PaymentCompleted(
                UUID.randomUUID(),
                Instant.now(),
                sagaId,
                paymentAttemptId,
                providerPaymentId
        );
        send(RoutingKeys.PAYMENT_COMPLETED, event);
        log.info("Published PaymentCompleted sagaId={} paymentAttemptId={} providerPaymentId={}",
                sagaId, paymentAttemptId, providerPaymentId);
    }

    public void publishPaymentFailed(UUID sagaId,
                                     long paymentAttemptId,
                                     PaymentFailed.Reason reason,
                                     String message) {
        PaymentFailed event = new PaymentFailed(
                UUID.randomUUID(),
                Instant.now(),
                sagaId,
                paymentAttemptId,
                reason,
                message
        );
        send(RoutingKeys.PAYMENT_FAILED, event);
        log.info("Published PaymentFailed sagaId={} paymentAttemptId={} reason={} message={}",
                sagaId, paymentAttemptId, reason, message);
    }

    private void send(String routingKey, Object payload) {
        rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE_PAYMENT_EVENTS, routingKey, payload);
    }
}