package com.n11.bootcamp.ecommerce.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(

        @NotNull
        Long productId,

        @NotNull
        @Min(value = 1, message = "quantity must be at least 1")
        Integer quantity
) {}