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

    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "checkout_token")
    private String checkoutToken;

    @Column(name = "payment_page_url")
    private String paymentPageUrl;

    @Column(name = "provider_payment_id")
    private String providerPaymentId;

    @Column(name = "failure_reason")
    private String failureReason;

    public static PaymentAttempt create(UUID sagaId,
                                        long orderId,
                                        String provider,
                                        Money amount,
                                        String checkoutToken,
                                        String paymentPageUrl) {
        PaymentAttempt attempt = new PaymentAttempt();
        attempt.sagaId = sagaId;
        attempt.orderId = orderId;
        attempt.provider = provider;
        attempt.amount = amount;
        attempt.status = PaymentStatus.INITIATED;
        attempt.checkoutToken = checkoutToken;
        attempt.paymentPageUrl = paymentPageUrl;
        return attempt;
    }

    public void markSucceeded(String providerPaymentId) {
        this.status = PaymentStatus.SUCCEEDED;
        this.providerPaymentId = providerPaymentId;
    }

    public void markFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
    }
}