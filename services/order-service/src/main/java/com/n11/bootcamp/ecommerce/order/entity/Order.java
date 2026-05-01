package com.n11.bootcamp.ecommerce.order.entity;

import com.n11.bootcamp.ecommerce.persistence.Auditable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(
        name = "orders",
        uniqueConstraints = @UniqueConstraint(name = "uq_orders_saga_id", columnNames = "saga_id"),
        indexes = {
                @Index(name = "ix_orders_user_id", columnList = "user_id"),
                @Index(name = "ix_orders_saga_state", columnList = "saga_state")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Order extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Owning user, by Keycloak {@code sub}. */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Saga correlation id.
     */
    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_state", nullable = false)
    private SagaState sagaState;

    /** Reason text when {@code sagaState = FAILED}. Null otherwise. */
    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "applied_coupon_code")
    private String appliedCouponCode;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "recipientName", column = @Column(name = "ship_recipient_name")),
            @AttributeOverride(name = "phoneNumber",   column = @Column(name = "ship_phone_number")),
            @AttributeOverride(name = "line1",         column = @Column(name = "ship_line1")),
            @AttributeOverride(name = "line2",         column = @Column(name = "ship_line2")),
            @AttributeOverride(name = "city",          column = @Column(name = "ship_city")),
            @AttributeOverride(name = "postalCode",    column = @Column(name = "ship_postal_code")),
            @AttributeOverride(name = "country",       column = @Column(name = "ship_country"))
    })
    private Address shippingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "recipientName", column = @Column(name = "bill_recipient_name")),
            @AttributeOverride(name = "phoneNumber",   column = @Column(name = "bill_phone_number")),
            @AttributeOverride(name = "line1",         column = @Column(name = "bill_line1")),
            @AttributeOverride(name = "line2",         column = @Column(name = "bill_line2")),
            @AttributeOverride(name = "city",          column = @Column(name = "bill_city")),
            @AttributeOverride(name = "postalCode",    column = @Column(name = "bill_postal_code")),
            @AttributeOverride(name = "country",       column = @Column(name = "bill_country"))
    })
    private Address billingAddress;

    @Embedded
    @AttributeOverride(name = "amount",   column = @Column(name = "subtotal_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "subtotal_currency"))
    private Money subtotal;

    @Embedded
    @AttributeOverride(name = "amount",   column = @Column(name = "cart_total_discount_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "cart_total_discount_currency"))
    private Money cartTotalDiscount;

    @Embedded
    @AttributeOverride(name = "amount",   column = @Column(name = "total_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "total_currency"))
    private Money total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineItem> lineItems = new ArrayList<>();

    public void addLineItem(OrderLineItem item) {
        item.setOrder(this);
        lineItems.add(item);
    }
}