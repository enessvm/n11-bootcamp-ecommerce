package com.n11.bootcamp.ecommerce.product.client.dto;

import java.util.List;

public record StockBatchResponse(
        List<Item> items
) {

    public record Item(
            long productId,
            StockStatus status,
            int availableQuantity
    ) {}
}