package com.n11.bootcamp.ecommerce.product.dto;

import com.n11.bootcamp.ecommerce.product.client.dto.StockStatus;
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

        // TODO: discount
        // Money effectivePrice,
        // DiscountBadge discountBadge,
        StockStatus stockStatus,
        // TODO: review
        //java.math.BigDecimal averageRating,
        //Integer reviewCount,

        Instant createdAt,
        Instant updatedAt,


        boolean stockUnavailable,
        boolean discountsUnavailable,
        boolean reviewsUnavailable
) {}