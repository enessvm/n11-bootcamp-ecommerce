package com.n11.bootcamp.ecommerce.order.repository;

import com.n11.bootcamp.ecommerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findBySagaId(UUID sagaId);
}