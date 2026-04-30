package com.n11.bootcamp.ecommerce.product.repository;

import com.n11.bootcamp.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndDeletedFalse(Long id);

    List<Product> findAllByIdInAndDeletedFalse(Collection<Long> ids);
}