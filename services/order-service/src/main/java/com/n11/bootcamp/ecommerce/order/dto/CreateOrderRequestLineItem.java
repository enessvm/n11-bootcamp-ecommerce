package com.n11.bootcamp.ecommerce.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequestLineItem(

        @NotNull
        Long productId,

        @NotNull @Min(1)
        Integer quantity
) {}