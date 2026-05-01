package com.n11.bootcamp.ecommerce.stock.controller;

import com.n11.bootcamp.ecommerce.stock.dto.StockAdjustRequest;
import com.n11.bootcamp.ecommerce.stock.dto.StockAdjustResponse;
import com.n11.bootcamp.ecommerce.stock.dto.StockBatchRequest;
import com.n11.bootcamp.ecommerce.stock.dto.StockBatchResponse;
import com.n11.bootcamp.ecommerce.stock.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/stock/batch")
    public StockBatchResponse batch(@Valid @RequestBody StockBatchRequest request) {
        return stockService.batch(request);
    }

    @PutMapping("/stock/{productId}")
    public StockAdjustResponse adjust(@PathVariable long productId,
                                      @Valid @RequestBody StockAdjustRequest request) {
        return stockService.adjustStock(productId, request.availableQuantity());
    }

}