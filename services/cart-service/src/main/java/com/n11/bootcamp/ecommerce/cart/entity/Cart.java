package com.n11.bootcamp.ecommerce.cart.entity;

import com.n11.bootcamp.ecommerce.persistence.Auditable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * One persistent cart per user, identified by Keycloak subject.
 * UNIQUE(keycloak_sub) enforces it.
 *
 * <p>Lazy-created on first
 *
 * <p>Items are managed via the {@code lineItems} association with
 * orphan removal — clearing the list (or items) deletes the rows.
 */
@Entity
@Table(
        name = "cart",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_cart_keycloak_sub",
                columnNames = "keycloak_sub"
        ),
        indexes = @Index(name = "ix_cart_keycloak_sub", columnList = "keycloak_sub")
)
@Getter
@Setter
@NoArgsConstructor
public class Cart extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_sub", nullable = false)
    private UUID keycloakSub;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = jakarta.persistence.FetchType.LAZY
    )
    private List<CartItem> lineItems = new ArrayList<>();

    /**
     * Static factory for a fresh empty cart.
     */
    public static Cart create(UUID keycloakSub) {
        Cart cart = new Cart();
        cart.keycloakSub = keycloakSub;
        return cart;
    }
}