package com.n11.bootcamp.ecommerce.promotion.event;

import com.n11.bootcamp.ecommerce.events.RoutingKeys;
import com.n11.bootcamp.ecommerce.events.promotion.PromotionFailed;
import com.n11.bootcamp.ecommerce.events.promotion.PromotionApplied;
import com.n11.bootcamp.ecommerce.events.promotion.PromotionReverted;
import com.n11.bootcamp.ecommerce.promotion.dto.ValidationFailure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Publishes saga reply events back to order-service.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishApplied(UUID sagaId,
                               Long promotionId,
                               String code,
                               BigDecimal cartDiscountAmount,
                               String currency) {
        PromotionApplied event = new PromotionApplied(
                UUID.randomUUID(),
                Instant.now(),
                sagaId,
                promotionId,
                code,
                cartDiscountAmount,
                currency
        );
        rabbitTemplate.convertAndSend(
                RoutingKeys.EXCHANGE_PROMOTION_EVENTS,
                RoutingKeys.PROMOTION_APPLIED,
                event
        );
        log.info("Published PromotionApplied sagaId={} code={} discount={} {}",
                sagaId, code, cartDiscountAmount, currency);
    }

    public void publishReverted(UUID sagaId, Long promotionId, String code) {
        PromotionReverted event = new PromotionReverted(
                UUID.randomUUID(),
                Instant.now(),
                sagaId,
                promotionId,
                code
        );
        rabbitTemplate.convertAndSend(
                RoutingKeys.EXCHANGE_PROMOTION_EVENTS,
                RoutingKeys.PROMOTION_REVERTED,
                event
        );
        log.info("Published PromotionReverted sagaId={} code={}", sagaId, code);
    }

    public void publishApplicationFailed(UUID sagaId, ValidationFailure reason) {
        PromotionFailed event = new PromotionFailed(
                UUID.randomUUID(),
                Instant.now(),
                sagaId,
                mapReason(reason),
                humanReadable(reason)
        );
        rabbitTemplate.convertAndSend(
                RoutingKeys.EXCHANGE_PROMOTION_EVENTS,
                RoutingKeys.PROMOTION_FAILED,
                event
        );
        log.info("Published PromotionApplicationFailed sagaId={} reason={}",
                sagaId, reason);
    }

    private PromotionFailed.Reason mapReason(ValidationFailure internal) {
        return switch (internal) {
            case NOT_FOUND          -> PromotionFailed.Reason.NOT_FOUND;
            case INACTIVE           -> PromotionFailed.Reason.INACTIVE;
            case NOT_YET_VALID      -> PromotionFailed.Reason.NOT_YET_VALID;
            case EXPIRED            -> PromotionFailed.Reason.EXPIRED;
            case MAX_USES_REACHED   -> PromotionFailed.Reason.MAX_USES_REACHED;
            case CART_BELOW_MINIMUM -> PromotionFailed.Reason.CART_BELOW_MINIMUM;
        };
    }

    private String humanReadable(ValidationFailure reason) {
        return switch (reason) {
            case NOT_FOUND          -> "Promotion code not found";
            case INACTIVE           -> "Promotion is not active";
            case NOT_YET_VALID      -> "Promotion is not yet valid";
            case EXPIRED            -> "Promotion has expired";
            case MAX_USES_REACHED   -> "Promotion has reached its maximum redemptions";
            case CART_BELOW_MINIMUM -> "Cart total is below the promotion's minimum";
        };
    }
}