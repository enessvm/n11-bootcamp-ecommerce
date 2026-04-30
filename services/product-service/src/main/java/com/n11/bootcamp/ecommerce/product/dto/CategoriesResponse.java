package com.n11.bootcamp.ecommerce.product.dto;

import java.util.List;

public record CategoriesResponse(
        List<CategoryResponse> categories
) {}