package com.n11.bootcamp.ecommerce.stock.service;

import com.n11.bootcamp.ecommerce.events.stock.CommitStockCommand;
import com.n11.bootcamp.ecommerce.events.stock.ReleaseStockCommand;
import com.n11.bootcamp.ecommerce.events.stock.ReserveStockCommand;
import com.n11.bootcamp.ecommerce.stock.dto.StockAdjustResponse;
import com.n11.bootcamp.ecommerce.stock.dto.StockBatchRequest;
import com.n11.bootcamp.ecommerce.stock.dto.StockBatchResponse;

public interface StockService {

    StockBatchResponse batch(StockBatchRequest request);

    StockAdjustResponse adjustStock(long productId, int availableQuantity);

    void initStock(Long productId);

    void consumeReserveCommand(ReserveStockCommand command);

    void consumeCommitCommand(CommitStockCommand command);

    void consumeReleaseCommand(ReleaseStockCommand command);
}