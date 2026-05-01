package com.n11.bootcamp.ecommerce.order.event;

import com.n11.bootcamp.ecommerce.events.stock.*;
import com.n11.bootcamp.ecommerce.order.config.RabbitMQConfig;
import com.n11.bootcamp.ecommerce.order.service.SagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes saga reply events from stock-service.
 * <p>Exceptions propagate up; retry policy + DLX handle the unhappy path.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = RabbitMQConfig.QUEUE_STOCK_REPLIES)
public class StockReplyListener {

    private final SagaService sagaService;

    @RabbitHandler
    public void onStockReserved(StockReserved event) {
        log.info("Received StockReserved sagaId={}", event.sagaId());
        sagaService.onStockReserved(event);
    }

    @RabbitHandler
    public void onStockReservationFailed(StockReservationFailed event) {
        log.info("Received StockReservationFailed sagaId={} reason={}",
                event.sagaId(), event.reason());
        sagaService.onStockReservationFailed(event);
    }

    @RabbitHandler
    public void onStockCommitted(StockCommitted event) {
        log.info("Received StockCommitted sagaId={}", event.sagaId());
        sagaService.onStockCommitted(event);
    }

    @RabbitHandler
    public void onStockCommitFailed(StockCommitFailed event) {
        log.info("Received StockCommitFailed sagaId={}", event.sagaId());
        sagaService.onStockCommitFailed(event);
    }

    @RabbitHandler
    public void onStockReleased(StockReleased event) {
        log.info("Received StockReleased sagaId={}", event.sagaId());
        sagaService.onStockReleased(event);
    }
}