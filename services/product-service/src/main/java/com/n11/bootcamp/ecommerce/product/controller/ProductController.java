package com.n11.bootcamp.ecommerce.product.controller;

import com.n11.bootcamp.ecommerce.product.dto.*;
import com.n11.bootcamp.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products/{id}")
    public ProductDetailResponse getById(@PathVariable Long id) {
        return productService.getById(id);
    }

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