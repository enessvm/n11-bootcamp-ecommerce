package com.n11.bootcamp.ecommerce.payment.entity;

import com.n11.bootcamp.ecommerce.persistence.Auditable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(
        name = "payment_attempt",
        uniqueConstraints = @UniqueConstraint(name = "uq_payment_attempt_saga", columnNames = "saga_id"),
        indexes = @Index(name = "ix_payment_attempt_order", columnList = "order_id")
)
@Getter
@Setter
@NoArgsConstructor
public class PaymentAttempt extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Saga identifier from order-service.
     */
    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Embedded
    @AttributeOverride(name = "amount",   column = @Column(name = "amount_amount",   nullable = false, precision = 10, scale = 2))
    @AttributeOverride(name = "currency", column = @Column(name = "amount_currency", nullable = false, length = 3))
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private PaymentProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    /** Provider's transaction reference. Null on failure. */
    @Column(name = "provider_txn_id")
    private String providerTxnId;

    /** Reason text from the provider. Null on success. */
    @Column(name = "failure_reason")
    private String failureReason;
}