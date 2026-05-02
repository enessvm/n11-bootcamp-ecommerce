package com.n11.bootcamp.ecommerce.product.dto;

import com.n11.bootcamp.ecommerce.product.entity.Money;

import java.time.Instant;
import java.util.List;

public record ProductDetailResponse(
        Long id,
        String name,
        String description,
        String brand,
        Long categoryId,
        List<CategoryPathEntry> categoryPath,
        List<ImageEntry> images,
        Money listPrice,
        StockStatus stockStatus,
        Instant createdAt,
        Instant updatedAt,
        boolean stockUnavailable
) {}
