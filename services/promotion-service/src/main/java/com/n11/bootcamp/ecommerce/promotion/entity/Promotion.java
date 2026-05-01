package com.n11.bootcamp.ecommerce.promotion.entity;

import com.n11.bootcamp.ecommerce.persistence.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "promotion")
@Getter
@Setter
@NoArgsConstructor
public class Promotion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    /**
     * For PERCENTAGE: 10.00 means 10% off.
     * For FIXED_AMOUNT: 25.00 means 25 currency-units off.
     * Control by discountType
     */
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false)
    private PromotionScope scope;

    /**
     * Minimum cart total required for this promo to apply. Null = no minimum.
     */
    @Column(name = "min_cart_total", precision = 10, scale = 2)
    private BigDecimal minCartTotal;

    /**
     * Null = unlimited uses.
     */
    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "times_redeemed", nullable = false)
    private int timesRedeemed = 0;

    @Column(name = "valid_from", nullable = false)
    private Instant validFrom;

    @Column(name = "valid_until", nullable = false)
    private Instant validUntil;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}