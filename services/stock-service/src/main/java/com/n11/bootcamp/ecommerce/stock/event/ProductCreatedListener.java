package com.n11.bootcamp.ecommerce.stock.event;

import com.n11.bootcamp.ecommerce.events.product.ProductCreated;
import com.n11.bootcamp.ecommerce.stock.config.RabbitMQConfig;
import com.n11.bootcamp.ecommerce.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCreatedListener {

    private final StockService stockService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PRODUCT_CREATED)
    public void onProductCreated(ProductCreated event) {
        log.info("Received ProductCreated eventId={} productId={}",
                event.eventId(), event.productId());
        stockService.initStock(event.productId());
    }
}