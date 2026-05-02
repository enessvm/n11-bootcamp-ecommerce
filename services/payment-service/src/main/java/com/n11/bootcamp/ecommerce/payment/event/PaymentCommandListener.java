package com.n11.bootcamp.ecommerce.payment.event;

import com.n11.bootcamp.ecommerce.events.payment.ChargePaymentCommand;
import com.n11.bootcamp.ecommerce.payment.config.RabbitMQConfig;
import com.n11.bootcamp.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes saga commands from order-service.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = RabbitMQConfig.QUEUE_SAGA_COMMANDS)
public class PaymentCommandListener {

    private final PaymentService paymentService;

    @RabbitHandler
    public void onCharge(ChargePaymentCommand command) {
        log.info("Received ChargePaymentCommand sagaId={} orderId={} amount={} {}",
                command.sagaId(), command.orderId(), command.amount(), command.currency());
        paymentService.consumeChargeCommand(command);
    }
}