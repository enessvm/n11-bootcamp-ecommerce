package com.n11.bootcamp.ecommerce.product.service.impl;

import com.n11.bootcamp.ecommerce.product.dto.CategoriesResponse;
import com.n11.bootcamp.ecommerce.product.dto.CategoryResponse;
import com.n11.bootcamp.ecommerce.product.entity.Category;
import com.n11.bootcamp.ecommerce.product.mapper.CategoryMapper;
import com.n11.bootcamp.ecommerce.product.repository.CategoryRepository;
import com.n11.bootcamp.ecommerce.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public CategoriesResponse getAllCategories() {
        List<Category> all = categoryRepository.findAll();

        // Index children by their parent's id; collect roots separately.
        Map<Long, List<Category>> childrenByParentId = new HashMap<>();
        List<Category> roots = new ArrayList<>();
        for (Category c : all) {
            if (c.getParent() == null) {
                roots.add(c);
            } else {
                childrenByParentId
                        .computeIfAbsent(c.getParent().getId(), k -> new ArrayList<>())
                        .add(c);
            }
        }

        List<CategoryResponse> rootResponses = roots.stream()
                .map(root -> buildTree(root, childrenByParentId))
                .toList();

        return new CategoriesResponse(rootResponses);
    }

    private CategoryResponse buildTree(Category node, Map<Long, List<Category>> childrenByParentId) {
        List<CategoryResponse> childResponses = childrenByParentId
                .getOrDefault(node.getId(), List.of())
                .stream()
                .map(child -> buildTree(child, childrenByParentId))
                .toList();
        return categoryMapper.toResponse(node, childResponses);
    }
}