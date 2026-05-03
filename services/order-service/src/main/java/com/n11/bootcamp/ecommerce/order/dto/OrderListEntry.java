package com.n11.bootcamp.ecommerce.order.dto;

import com.n11.bootcamp.ecommerce.order.entity.Money;
import com.n11.bootcamp.ecommerce.order.entity.SagaState;

import java.time.Instant;

public record OrderListEntry(
        Long id,
        SagaState sagaState,
        Money total,
        int itemCount,
        String firstItemName,
        String firstItemImageUrl,
        Instant createdAt
) {}
