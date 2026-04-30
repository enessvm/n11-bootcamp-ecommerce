package com.n11.bootcamp.ecommerce.stock.service;

import com.n11.bootcamp.ecommerce.stock.dto.StockBatchRequest;
import com.n11.bootcamp.ecommerce.stock.dto.StockBatchResponse;

public interface StockService {

    StockBatchResponse batch(StockBatchRequest request);

    void initStock(Long productId);
}