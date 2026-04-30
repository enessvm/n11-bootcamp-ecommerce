package com.n11.bootcamp.ecommerce.product.controller;

import com.n11.bootcamp.ecommerce.product.dto.BatchProductsRequest;
import com.n11.bootcamp.ecommerce.product.dto.CreateProductRequest;
import com.n11.bootcamp.ecommerce.product.dto.ProductBatchResponse;
import com.n11.bootcamp.ecommerce.product.dto.ProductResponse;
import com.n11.bootcamp.ecommerce.product.dto.UpdateProductRequest;
import com.n11.bootcamp.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse created = productService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/products/{id}")
    public ProductResponse update(@PathVariable Long id,
                                  @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.softDelete(id);
    }

    @PostMapping("/products/batch")
    public ProductBatchResponse batch(@Valid @RequestBody BatchProductsRequest request) {
        return productService.batch(request);
    }
}