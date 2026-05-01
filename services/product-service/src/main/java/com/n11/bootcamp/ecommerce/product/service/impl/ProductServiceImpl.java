package com.n11.bootcamp.ecommerce.product.service.impl;

import com.n11.bootcamp.ecommerce.product.client.StockServiceClient;
import com.n11.bootcamp.ecommerce.product.client.dto.StockBatchRequest;
import com.n11.bootcamp.ecommerce.product.client.dto.StockBatchResponse;
import com.n11.bootcamp.ecommerce.product.dto.StockStatus;
import com.n11.bootcamp.ecommerce.product.dto.*;
import com.n11.bootcamp.ecommerce.product.entity.Category;
import com.n11.bootcamp.ecommerce.product.entity.Product;
import com.n11.bootcamp.ecommerce.product.event.ProductEventPublisher;
import com.n11.bootcamp.ecommerce.product.mapper.ProductEventMapper;
import com.n11.bootcamp.ecommerce.product.mapper.ProductMapper;
import com.n11.bootcamp.ecommerce.product.repository.CategoryRepository;
import com.n11.bootcamp.ecommerce.product.repository.ProductRepository;
import com.n11.bootcamp.ecommerce.product.repository.ProductSpecifications;
import com.n11.bootcamp.ecommerce.product.service.ProductService;
import com.n11.bootcamp.ecommerce.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductEventMapper productEventMapper;
    private final ProductEventPublisher eventPublisher;
    private final StockServiceClient stockServiceClient;

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getById(Long id) {
        Product product = loadProductOrThrow(id);

        StockStatus stockStatus;
        boolean stockUnavailable;

        try {
            StockBatchResponse response = stockServiceClient.batch(
                    new StockBatchRequest(List.of(id)));

            stockStatus = response.items().stream()
                    .filter(item -> item.productId() == id)
                    .findFirst()
                    .map(StockBatchResponse.Item::status)
                    .orElse(StockStatus.OUT_OF_STOCK); // responded but missing → OUT_OF_STOCK
            stockUnavailable = false;
        } catch (Exception e) {
            log.warn("stock-service enrichment failed for productId={}, degrading", id, e);
            stockStatus = null;
            stockUnavailable = true;
        }

        return productMapper.toDetailResponse(product, stockStatus, stockUnavailable);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductListResponse listProducts(int page, int size,
                                            String sortField, Sort.Direction sortDirection,
                                            Long categoryId, String q,
                                            BigDecimal minPrice, BigDecimal maxPrice) {

        Specification<Product> spec = ProductSpecifications.notDeleted();
        if (categoryId != null) {
            spec = spec.and(ProductSpecifications.categoryIs(categoryId));
        }
        if (q != null && !q.isBlank()) {
            spec = spec.and(ProductSpecifications.nameOrDescriptionContains(q));
        }
        if (minPrice != null) {
            spec = spec.and(ProductSpecifications.priceAtLeast(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(ProductSpecifications.priceAtMost(maxPrice));
        }

        Sort.Order order = sortField.equals("listPrice")
                ? new Sort.Order(sortDirection, "listPrice.amount")
                : new Sort.Order(sortDirection, sortField);

        Pageable pageable = PageRequest.of(page, size, Sort.by(order));
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        Map<Long, StockStatus> stockByProductId = new HashMap<>();
        boolean stockUnavailable = false;

        if (!productPage.isEmpty()) {
            try {
                StockBatchResponse stockResponse = stockServiceClient.batch(
                        new StockBatchRequest(
                                productPage.getContent().stream().map(Product::getId).toList()));

                for (StockBatchResponse.Item item : stockResponse.items()) {
                    stockByProductId.put(item.productId(), item.status());
                }
            } catch (Exception e) {
                log.warn("stock-service batch enrichment failed for listing, degrading", e);
                stockUnavailable = true;
            }
        }

        final Map<Long, StockStatus> finalStockMap = stockByProductId;
        final boolean finalStockUnavailable = stockUnavailable;

        List<ProductListEntry> entries = productPage.getContent().stream()
                .map(p -> {
                    StockStatus status = finalStockUnavailable
                            ? null
                            : finalStockMap.getOrDefault(p.getId(), StockStatus.OUT_OF_STOCK);
                    return productMapper.toListEntry(p, status);
                })
                .toList();

        return new ProductListResponse(
                entries,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast(),
                finalStockUnavailable,
                true,
                true
        );
    }

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Category category = loadCategory(request.categoryId());
        Product entity = productMapper.toEntity(request, category);
        Product saved = productRepository.save(entity);

        eventPublisher.publishCreated(productEventMapper.toCreatedEvent(saved));
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product existing = loadProductOrThrow(id);
        Category category = loadCategory(request.categoryId());
        productMapper.updateEntity(existing, request, category);

        return productMapper.toResponse(existing);
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        Product existing = loadProductOrThrow(id);
        existing.setDeleted(true);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductBatchResponse batch(BatchProductsRequest request) {
        List<Product> found = productRepository.findAllByIdInAndDeletedFalse(request.ids());
        List<ProductBatchResponse.BatchEntry> entries = found.stream()
                .map(productMapper::toBatchEntry)
                .toList();
        return new ProductBatchResponse(entries);
    }

    // --- helpers ---

    private Category loadCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
    }

    private Product loadProductOrThrow(Long id) {
        return productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}