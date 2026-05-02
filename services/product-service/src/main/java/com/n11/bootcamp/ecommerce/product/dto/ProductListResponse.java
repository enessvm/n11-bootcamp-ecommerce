package com.n11.bootcamp.ecommerce.product.dto;

import java.util.List;

/**
 * Paginated listing response for {@code GET /products}. Per-endpoint envelope
 * with degradation flags.
 */
public record ProductListResponse(
        List<ProductListEntry> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean stockUnavailable
) {}
