package com.n11.bootcamp.ecommerce.product.service;

import com.n11.bootcamp.ecommerce.product.dto.*;

public interface ProductService {

    ProductResponse create(CreateProductRequest request);

    ProductResponse update(Long id, UpdateProductRequest request);

    void softDelete(Long id);

    ProductBatchResponse batch(BatchProductsRequest request);

    ProductDetailResponse getById(Long id);
}