package com.n11.bootcamp.ecommerce.cart.client.dto;

import com.n11.bootcamp.ecommerce.cart.dto.Money;

import java.time.Instant;
import java.util.List;

public record ProductBatchResponse(
        List<BatchEntry> products
) {

    public record BatchEntry(
            Long id,
            String name,
            String description,
            String brand,
            Long categoryId,
            String categoryName,
            String primaryImageUrl,
            Money listPrice,
            Instant createdAt,
            Instant updatedAt
    ) {}
}