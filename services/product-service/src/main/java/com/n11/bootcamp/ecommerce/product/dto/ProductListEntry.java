package com.n11.bootcamp.ecommerce.product.dto;

import com.n11.bootcamp.ecommerce.product.entity.Money;


public record ProductListEntry(
        Long id,
        String name,
        String brand,
        Long categoryId,
        String primaryImageUrl,
        Money listPrice,
        StockStatus stockStatus
) {}
