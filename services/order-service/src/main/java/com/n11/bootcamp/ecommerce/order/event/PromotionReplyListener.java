package com.n11.bootcamp.ecommerce.order.event;

import com.n11.bootcamp.ecommerce.events.promotion.PromotionFailed;
import com.n11.bootcamp.ecommerce.events.promotion.PromotionApplied;
import com.n11.bootcamp.ecommerce.order.config.RabbitMQConfig;
import com.n11.bootcamp.ecommerce.order.service.SagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes saga reply events from promotion-service.
 * <p>Exceptions propagate up; retry policy + DLX handle the unhappy path.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = RabbitMQConfig.QUEUE_PROMOTION_REPLIES)
public class PromotionReplyListener {

    private final SagaService sagaService;

    @RabbitHandler
    public void onPromotionApplied(PromotionApplied event) {
        log.info("Received PromotionApplied sagaId={} code={} discount={} {}",
                event.sagaId(), event.code(),
                event.cartDiscountAmount(), event.currency());
        sagaService.onPromotionApplied(event);
    }

    @RabbitHandler
    public void onPromotionApplicationFailed(PromotionFailed event) {
        log.info("Received PromotionApplicationFailed sagaId={} reason={}",
                event.sagaId(), event.reason());
        sagaService.onPromotionApplicationFailed(event);
    }
}