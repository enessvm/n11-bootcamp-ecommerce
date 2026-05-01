package com.n11.bootcamp.ecommerce.product.service;

import com.n11.bootcamp.ecommerce.product.dto.*;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;

public interface ProductService {

    ProductResponse create(CreateProductRequest request);

    ProductResponse update(Long id, UpdateProductRequest request);

    void softDelete(Long id);

    ProductBatchResponse batch(BatchProductsRequest request);

    ProductDetailResponse getById(Long id);

    ProductListResponse listProducts(
            int page, int size, String sortField, Sort.Direction sortDirection,
            Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice
    );
}