package com.n11.bootcamp.ecommerce.cart.client.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ProductBatchRequest(
        @NotEmpty
        List<Long> ids
) {}