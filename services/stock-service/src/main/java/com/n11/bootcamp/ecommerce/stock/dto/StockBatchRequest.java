package com.n11.bootcamp.ecommerce.stock.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StockBatchRequest(
        
        List<@NotNull Long> productIds
) {}