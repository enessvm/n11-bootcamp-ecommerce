package com.n11.bootcamp.ecommerce.stock.entity;

import com.n11.bootcamp.ecommerce.persistence.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock_level")
@Getter
@Setter
@NoArgsConstructor()
public class StockLevel extends Auditable {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity = 0;
}