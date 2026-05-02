package com.n11.bootcamp.ecommerce.cart.dto;

import java.time.Instant;
import java.util.List;

/**
 * Enriched cart view returned by {@code GET /cart}. Items include both
 * stored data (productId, quantity) and live data fetched from
 * product-service (name, price, image).
 */
public record CartResponse(
        Long cartId,
        List<Item> items,
        Money subtotal,
        Instant updatedAt
) {

    /**
     * Per-line-item view with stored + enriched fields. {@code lineTotal}
     * is computed (price × quantity) for display convenience.
     */
    public record Item(
            Long itemId,
            long productId,
            String productName,
            String productBrand,
            String productImageUrl,
            Money unitPrice,
            int quantity,
            Money lineTotal
    ) {}

}