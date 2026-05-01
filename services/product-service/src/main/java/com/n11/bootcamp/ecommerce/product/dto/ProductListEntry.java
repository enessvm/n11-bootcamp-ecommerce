package com.n11.bootcamp.ecommerce.product.dto;

import com.n11.bootcamp.ecommerce.product.entity.Money;


public record ProductListEntry(
        Long id,
        String name,
        String brand,
        Long categoryId,
        String primaryImageUrl,
        Money listPrice,

        // TODO:  discount
        // Money effectivePrice,
        // ProductDetailResponse.DiscountBadge discountBadge,
        StockStatus stockStatus
        // TODO: review
        // BigDecimal averageRating,
        // Integer reviewCount
) {}