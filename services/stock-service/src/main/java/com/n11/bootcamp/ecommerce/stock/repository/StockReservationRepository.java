package com.n11.bootcamp.ecommerce.stock.repository;

import com.n11.bootcamp.ecommerce.stock.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    List<StockReservation> findBySagaId(UUID sagaId);

    boolean existsBySagaId(UUID sagaId);
}