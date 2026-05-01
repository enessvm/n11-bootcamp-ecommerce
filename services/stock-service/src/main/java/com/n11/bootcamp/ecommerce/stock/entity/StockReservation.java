package com.n11.bootcamp.ecommerce.stock.entity;

import com.n11.bootcamp.ecommerce.persistence.Auditable;
import jakarta.persistence.Column;
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

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(
        name = "stock_reservation",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_stock_reservation_saga_product",
                columnNames = {"saga_id", "product_id"}
        ),
        indexes = {
                @Index(name = "ix_stock_reservation_saga_id", columnList = "saga_id"),
                @Index(name = "ix_stock_reservation_status_expires_at",
                        columnList = "status, expires_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class StockReservation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;

    @Column(name = "product_id", nullable = false)
    private long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;


    public static StockReservation create(UUID sagaId,
                                          long productId,
                                          int quantity,
                                          Instant expiresAt) {
        StockReservation reservation = new StockReservation();
        reservation.sagaId = sagaId;
        reservation.productId = productId;
        reservation.quantity = quantity;
        reservation.status = ReservationStatus.RESERVED;
        reservation.expiresAt = expiresAt;
        return reservation;
    }
}