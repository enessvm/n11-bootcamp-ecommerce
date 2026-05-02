package com.n11.bootcamp.ecommerce.cart.entity;

import com.n11.bootcamp.ecommerce.persistence.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(
        name = "cart_item",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_cart_item_cart_product",
                columnNames = {"cart_id", "product_id"}
        ),
        indexes = @Index(name = "ix_cart_item_cart_id", columnList = "cart_id")
)
@Getter
@Setter
@NoArgsConstructor
public class CartItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "product_id", nullable = false)
    private long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public static CartItem create(Cart cart, long productId, int quantity) {
        CartItem item = new CartItem();
        item.cart = cart;
        item.productId = productId;
        item.quantity = quantity;
        return item;
    }
}