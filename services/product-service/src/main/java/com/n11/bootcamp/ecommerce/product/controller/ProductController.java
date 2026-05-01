package com.n11.bootcamp.ecommerce.product.controller;

import com.n11.bootcamp.ecommerce.product.dto.*;
import com.n11.bootcamp.ecommerce.product.exception.InvalidQueryParamException;
import com.n11.bootcamp.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("createdAt", "name", "listPrice");

    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 100;

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


    @GetMapping("/products")
    public ProductListResponse listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        validatePageSize(size);
        SortSpec sortSpec = parseSort(sort);

        return productService.listProducts(
                page, size,
                sortSpec.field(), sortSpec.direction(),
                categoryId, q, minPrice, maxPrice
        );
    }

    private void validatePageSize(int size) {
        if (size < MIN_PAGE_SIZE || size > MAX_PAGE_SIZE) {
            throw new InvalidQueryParamException(
                    "size must be between " + MIN_PAGE_SIZE + " and " + MAX_PAGE_SIZE);
        }
    }

    private SortSpec parseSort(String sort) {
        String[] parts = sort.split(",");
        if (parts.length != 2) {
            throw new InvalidQueryParamException(
                    "sort must be 'field,direction' (e.g. 'createdAt,desc')");
        }
        String field = parts[0].trim();
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            throw new InvalidQueryParamException(
                    "sort field must be one of " + ALLOWED_SORT_FIELDS);
        }
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(parts[1].trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidQueryParamException(
                    "sort direction must be 'asc' or 'desc'");
        }
        return new SortSpec(field, direction);
    }

    private record SortSpec(String field, Sort.Direction direction) {}
}