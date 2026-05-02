package com.n11.bootcamp.ecommerce.order.event;

import com.n11.bootcamp.ecommerce.events.payment.PaymentCompleted;
import com.n11.bootcamp.ecommerce.events.payment.PaymentFailed;
import com.n11.bootcamp.ecommerce.events.payment.PaymentInitiated;
import com.n11.bootcamp.ecommerce.order.config.RabbitMQConfig;
import com.n11.bootcamp.ecommerce.order.service.SagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes saga reply events from payment-service.
 * <p>Exceptions propagate up; retry policy + DLX handle the unhappy path.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = RabbitMQConfig.QUEUE_PAYMENT_REPLIES)
public class PaymentReplyListener {

    private final SagaService sagaService;

    @RabbitHandler
    public void onPaymentInitiated(PaymentInitiated event) {
        log.info("Received PaymentInitiated sagaId={} paymentAttemptId={}",
                event.sagaId(), event.paymentAttemptId());
        sagaService.onPaymentInitiated(event);
    }

    @RabbitHandler
    public void onPaymentCompleted(PaymentCompleted event) {
        log.info("Received PaymentCompleted sagaId={} paymentAttemptId={} providerPaymentId={}",
                event.sagaId(), event.paymentAttemptId(), event.providerPaymentId());
        sagaService.onPaymentCompleted(event);
    }

    @RabbitHandler
    public void onPaymentFailed(PaymentFailed event) {
        log.info("Received PaymentFailed sagaId={} paymentAttemptId={} reason={}",
                event.sagaId(), event.paymentAttemptId(), event.reason());
        sagaService.onPaymentFailed(event);
    }
}