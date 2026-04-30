package com.n11.bootcamp.ecommerce.product.service;

import com.n11.bootcamp.ecommerce.product.dto.BatchProductsRequest;
import com.n11.bootcamp.ecommerce.product.dto.CreateProductRequest;
import com.n11.bootcamp.ecommerce.product.dto.ProductBatchResponse;
import com.n11.bootcamp.ecommerce.product.dto.ProductResponse;
import com.n11.bootcamp.ecommerce.product.dto.UpdateProductRequest;

public interface ProductService {

    ProductResponse create(CreateProductRequest request);

    ProductResponse update(Long id, UpdateProductRequest request);

    void softDelete(Long id);

    ProductBatchResponse batch(BatchProductsRequest request);
}