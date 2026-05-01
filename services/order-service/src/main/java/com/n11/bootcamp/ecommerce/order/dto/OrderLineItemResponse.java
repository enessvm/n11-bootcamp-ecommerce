package com.n11.bootcamp.ecommerce.order.dto;

import com.n11.bootcamp.ecommerce.order.entity.Money;

public record OrderLineItemResponse(
        Long id,
        Long productId,
        String productName,
        String productBrand,
        String primaryImageUrl,
        int quantity,
        Money unitListPrice,
        Money unitEffectivePrice,
        Money lineTotal,
        String appliedPromotionCode
) {}