package com.n11.bootcamp.ecommerce.order.client.dto;

import com.n11.bootcamp.ecommerce.order.entity.Money;

public record ProductBatchEntry(
        Long id,
        String name,
        String brand,
        String categoryName,
        String primaryImageUrl,
        Money listPrice
) {}