package com.n11.bootcamp.ecommerce.stock.service.impl;

import com.n11.bootcamp.ecommerce.stock.dto.StockBatchRequest;
import com.n11.bootcamp.ecommerce.stock.dto.StockBatchResponse;
import com.n11.bootcamp.ecommerce.stock.entity.StockLevel;
import com.n11.bootcamp.ecommerce.stock.mapper.StockMapper;
import com.n11.bootcamp.ecommerce.stock.repository.StockLevelRepository;
import com.n11.bootcamp.ecommerce.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockLevelRepository stockLevelRepository;
    private final StockMapper stockMapper;

    @Override
    @Transactional(readOnly = true)
    public StockBatchResponse batch(StockBatchRequest request) {
        List<StockLevel> found = stockLevelRepository.findAllByProductIdIn(request.productIds());
        List<StockBatchResponse.Item> items = found.stream()
                .map(stockMapper::toBatchItem)
                .toList();
        return new StockBatchResponse(items);
    }

    @Override
    @Transactional
    public void initStock(Long productId) {
        stockLevelRepository.insertIfAbsent(productId, Instant.now());
    }
}