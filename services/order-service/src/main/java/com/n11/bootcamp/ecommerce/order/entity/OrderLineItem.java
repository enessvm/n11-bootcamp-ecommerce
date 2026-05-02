package com.n11.bootcamp.ecommerce.order.entity;

import com.n11.bootcamp.ecommerce.persistence.Auditable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "order_line_item")
@Getter
@Setter
@NoArgsConstructor
public class OrderLineItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** Snapshotted product name — product-service's name may change later. */
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_brand")
    private String productBrand;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "primary_image_url")
    private String primaryImageUrl;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    /** List price per unit at order time. */
    @Embedded
    @AttributeOverride(name = "amount",   column = @Column(name = "unit_list_price_amount",   nullable = false, precision = 10, scale = 2))
    @AttributeOverride(name = "currency", column = @Column(name = "unit_list_price_currency", nullable = false, length = 3))
    private Money unitListPrice;

    /**
     * Effective price per unit after promotion. Equals {@code unitListPrice}
     * when no promotion applied. Slice 1a doesn't have promotion-service yet
     * so this is always {@code unitListPrice}.
     */
    @Embedded
    @AttributeOverride(name = "amount",   column = @Column(name = "unit_effective_price_amount",   nullable = false, precision = 10, scale = 2))
    @AttributeOverride(name = "currency", column = @Column(name = "unit_effective_price_currency", nullable = false, length = 3))
    private Money unitEffectivePrice;

    /** {@code unitEffectivePrice * quantity}. Computed at line-creation time, snapshot. */
    @Embedded
    @AttributeOverride(name = "amount",   column = @Column(name = "line_total_amount",   nullable = false, precision = 10, scale = 2))
    @AttributeOverride(name = "currency", column = @Column(name = "line_total_currency", nullable = false, length = 3))
    private Money lineTotal;

    /** Set when a line-scope promotion applied. Slice 1a: always null. */
    @Column(name = "applied_promotion_code")
    private String appliedPromotionCode;
}