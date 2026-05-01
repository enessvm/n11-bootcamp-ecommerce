package com.n11.bootcamp.ecommerce.stock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockAdjustRequest(

        @NotNull
        @Min(value = 0, message = "availableQuantity must be non-negative")
        Integer availableQuantity
) {}