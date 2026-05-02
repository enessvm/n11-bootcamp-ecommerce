package com.n11.bootcamp.ecommerce.order.event;

import com.n11.bootcamp.ecommerce.events.RoutingKeys;
import com.n11.bootcamp.ecommerce.events.payment.ChargePaymentCommand;
import com.n11.bootcamp.ecommerce.events.promotion.ApplyPromotionCommand;
import com.n11.bootcamp.ecommerce.events.promotion.RevertPromotionCommand;
import com.n11.bootcamp.ecommerce.events.stock.CommitStockCommand;
import com.n11.bootcamp.ecommerce.events.stock.ReleaseStockCommand;
import com.n11.bootcamp.ecommerce.events.stock.ReserveStockCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes saga commands to participant services.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SagaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishReserveStock(ReserveStockCommand command) {
        rabbitTemplate.convertAndSend(
                RoutingKeys.EXCHANGE_STOCK_COMMANDS,
                RoutingKeys.STOCK_RESERVE,
                command
        );
        log.info("Published ReserveStockCommand sagaId={} orderId={}",
                command.sagaId(), command.orderId());
    }

    public void publishApplyPromotion(ApplyPromotionCommand command) {
        rabbitTemplate.convertAndSend(
                RoutingKeys.EXCHANGE_PROMOTION_COMMANDS,
                RoutingKeys.PROMOTION_APPLY,
                command
        );
        log.info("Published ApplyPromotionCommand sagaId={} orderId={} code={}",
                command.sagaId(), command.orderId(), command.code());
    }

    public void publishRevertPromotion(RevertPromotionCommand command) {
        rabbitTemplate.convertAndSend(
                RoutingKeys.EXCHANGE_PROMOTION_COMMANDS,
                RoutingKeys.PROMOTION_REVERT,
                command
        );
        log.info("Published RevertPromotionCommand sagaId={}", command.sagaId());
    }

    public void publishCommitStock(CommitStockCommand command) {
        rabbitTemplate.convertAndSend(
                RoutingKeys.EXCHANGE_STOCK_COMMANDS,
                RoutingKeys.STOCK_COMMIT,
                command
        );
        log.info("Published CommitStockCommand sagaId={} orderId={}",
                command.sagaId(), command.orderId());
    }

    public void publishReleaseStock(ReleaseStockCommand command) {
        rabbitTemplate.convertAndSend(
                RoutingKeys.EXCHANGE_STOCK_COMMANDS,
                RoutingKeys.STOCK_RELEASE,
                command
        );
        log.info("Published ReleaseStockCommand sagaId={} orderId={}",
                command.sagaId(), command.orderId());
    }

    public void publishChargePayment(ChargePaymentCommand command) {
        rabbitTemplate.convertAndSend(
                RoutingKeys.EXCHANGE_PAYMENT_COMMANDS,
                RoutingKeys.PAYMENT_CHARGE,
                command
        );
        log.info("Published ChargePaymentCommand sagaId={} orderId={}",
                command.sagaId(), command.orderId());
    }

}