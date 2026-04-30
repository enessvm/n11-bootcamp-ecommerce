package com.n11.bootcamp.ecommerce.product.mapper;

import com.n11.bootcamp.ecommerce.product.dto.CategoryResponse;
import com.n11.bootcamp.ecommerce.product.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category, List<CategoryResponse> children) {
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                parentId,
                children
        );
    }
}