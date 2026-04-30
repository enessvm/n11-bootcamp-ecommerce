package com.n11.bootcamp.ecommerce.product.dto;

import com.n11.bootcamp.ecommerce.product.entity.Money;

import java.time.Instant;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String brand,
        Long categoryId,
        List<CategoryPathEntry> categoryPath,
        List<ImageEntry> images,
        Money listPrice,
        Instant createdAt,
        Instant updatedAt
) {}