package com.n11.bootcamp.ecommerce.product.event;

import com.n11.bootcamp.ecommerce.events.RoutingKeys;
import com.n11.bootcamp.ecommerce.events.product.ProductCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes product-domain events to the {@code product.events} exchange.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishCreated(ProductCreated event) {
        rabbitTemplate.convertAndSend(
                RoutingKeys.EXCHANGE_PRODUCT_EVENTS,
                RoutingKeys.PRODUCT_CREATED,
                event
        );
        log.info("Published ProductCreated eventId={} productId={}",
                event.eventId(), event.productId());
    }
}