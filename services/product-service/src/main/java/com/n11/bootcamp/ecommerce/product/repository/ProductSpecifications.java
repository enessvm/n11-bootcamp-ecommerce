package com.n11.bootcamp.ecommerce.product.repository;

import com.n11.bootcamp.ecommerce.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Composable {@link Specification} predicates for {@link Product} queries.
 * Used by {@code GET /products} to build dynamic WHERE clauses based on
 * which filter parameters the caller supplied.
 *
 * <p>Each method returns a self-contained predicate; combine via
 * {@code Specification.where(...).and(...)} at the call site.
 */
public final class ProductSpecifications {

    private ProductSpecifications() {}

    /** Filter out soft-deleted rows. Always applied. */
    public static Specification<Product> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    /** Match products in a specific category. Sub-categories are NOT included. */
    public static Specification<Product> categoryIs(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    /** Case-insensitive substring match against name OR description. */
    public static Specification<Product> nameOrDescriptionContains(String q) {
        return (root, query, cb) -> {
            String pattern = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    /** {@code listPrice.amount >= min}. */
    public static Specification<Product> priceAtLeast(BigDecimal min) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("listPrice").get("amount"), min);
    }

    /** {@code listPrice.amount <= max}. */
    public static Specification<Product> priceAtMost(BigDecimal max) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("listPrice").get("amount"), max);
    }
}