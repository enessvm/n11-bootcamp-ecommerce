package com.n11.bootcamp.ecommerce.product.controller;

import com.n11.bootcamp.ecommerce.product.dto.CategoriesResponse;
import com.n11.bootcamp.ecommerce.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public CategoriesResponse getAllCategories() {
        return categoryService.getAllCategories();
    }
}