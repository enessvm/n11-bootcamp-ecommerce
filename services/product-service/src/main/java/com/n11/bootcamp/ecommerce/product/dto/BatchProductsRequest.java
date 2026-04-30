package com.n11.bootcamp.ecommerce.product.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BatchProductsRequest(

        List<@NotNull Long> ids
) {}