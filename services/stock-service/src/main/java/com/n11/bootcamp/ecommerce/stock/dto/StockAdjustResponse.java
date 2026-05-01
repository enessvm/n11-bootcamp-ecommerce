package com.n11.bootcamp.ecommerce.stock.dto;

import java.time.Instant;

public record StockAdjustResponse(
        long productId,
        int availableQuantity,
        Instant updatedAt
) {}