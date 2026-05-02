package com.n11.bootcamp.ecommerce.payment.repository;

import com.n11.bootcamp.ecommerce.payment.entity.PaymentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> {

    Optional<PaymentAttempt> findBySagaId(UUID sagaId);

    boolean existsBySagaId(UUID sagaId);

    Optional<PaymentAttempt> findByCheckoutToken(String checkoutToken);
}