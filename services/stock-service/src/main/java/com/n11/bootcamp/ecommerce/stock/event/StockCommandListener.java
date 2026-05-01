package com.n11.bootcamp.ecommerce.stock.event;

import com.n11.bootcamp.ecommerce.events.stock.CommitStockCommand;
import com.n11.bootcamp.ecommerce.events.stock.ReleaseStockCommand;
import com.n11.bootcamp.ecommerce.events.stock.ReserveStockCommand;
import com.n11.bootcamp.ecommerce.stock.config.RabbitMQConfig;
import com.n11.bootcamp.ecommerce.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes saga commands from order-service.
 * Exceptions propagate; retry policy + DLX handle unhappy paths.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = RabbitMQConfig.QUEUE_SAGA_COMMANDS)
public class StockCommandListener {

    private final StockService stockService;

    @RabbitHandler
    public void onReserve(ReserveStockCommand command) {
        log.info("Received ReserveStockCommand sagaId={} orderId={} items={}",
                command.sagaId(), command.orderId(), command.items().size());
        stockService.consumeReserveCommand(command);
    }

    @RabbitHandler
    public void onCommit(CommitStockCommand command) {
        log.info("Received CommitStockCommand sagaId={} orderId={}",
                command.sagaId(), command.orderId());
        stockService.consumeCommitCommand(command);
    }

    @RabbitHandler
    public void onRelease(ReleaseStockCommand command) {
        log.info("Received ReleaseStockCommand sagaId={} orderId={}",
                command.sagaId(), command.orderId());
        stockService.consumeReleaseCommand(command);
    }
}