package com.n11.bootcamp.ecommerce.cart.repository;

import com.n11.bootcamp.ecommerce.cart.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, Long> {
    /**
     * With items eagerly fetched. Used by {@code GET /cart} so we don't
     * trigger an N+1 query when iterating items.
     */
    @EntityGraph(attributePaths = "lineItems")
    Optional<Cart> findByKeycloakSub(UUID keycloakSub);
}