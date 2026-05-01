package com.n11.bootcamp.ecommerce.promotion.event;

import com.n11.bootcamp.ecommerce.events.promotion.ApplyPromotionCommand;
import com.n11.bootcamp.ecommerce.promotion.config.RabbitMQConfig;
import com.n11.bootcamp.ecommerce.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes saga commands from order-service.
 *
 * <p>Exceptions propagate up; retry policy + DLX handle the unhappy path.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionCommandListener {

    private final PromotionService promotionService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_APPLY_COMMANDS)
    public void onApplyPromotion(ApplyPromotionCommand command) {
        log.info("Received ApplyPromotionCommand sagaId={} code={} cartTotal={} {}",
                command.sagaId(), command.code(),
                command.cartTotal(), command.currency());
        promotionService.consumeApplyCommand(command);
    }
}