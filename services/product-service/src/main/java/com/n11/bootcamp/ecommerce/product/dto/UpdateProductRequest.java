package com.n11.bootcamp.ecommerce.product.dto;

import com.n11.bootcamp.ecommerce.product.entity.Money;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateProductRequest(

        @NotBlank @Size(max = 200)
        String name,

        @NotBlank @Size(max = 5000)
        String description,

        @NotBlank @Size(max = 100)
        String brand,

        @NotNull
        Long categoryId,

        @NotNull @Valid
        Money listPrice,

        @NotBlank @Size(max = 1000)
        String primaryImageUrl,

        List<@NotBlank @Size(max = 1000) String> additionalImageUrls
) {}