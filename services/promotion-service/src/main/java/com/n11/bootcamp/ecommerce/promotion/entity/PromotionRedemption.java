package com.n11.bootcamp.ecommerce.promotion.entity;

import com.n11.bootcamp.ecommerce.persistence.Auditable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Per-saga redemption record. Acts as an idempotency table for the apply
 * command consumer — UNIQUE(saga_id) ensures a saga can redeem at most once.
 * On redelivery, the existing row is read and its stored discount amount is
 * republished as {@code PromotionApplied}.
 */
@Entity
@Table(
        name = "promotion_redemption",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_promotion_redemption_saga_id",
                columnNames = "saga_id"
        ),
        indexes = @Index(
                name = "ix_promotion_redemption_promotion_id",
                columnList = "promotion_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class PromotionRedemption extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;


    @Column(name = "promotion_id", nullable = false)
    private Long promotionId;


    @Embedded
    @AttributeOverride(name = "amount",   column = @Column(name = "cart_discount_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "cart_discount_currency"))
    private Money cartDiscount;
}