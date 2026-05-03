package com.n11.bootcamp.ecommerce.order.repository;

import com.n11.bootcamp.ecommerce.order.entity.Order;
import com.n11.bootcamp.ecommerce.order.entity.SagaState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findBySagaId(UUID sagaId);

    Page<Order> findByUserIdAndSagaState(UUID userId, SagaState sagaState, Pageable pageable);
}