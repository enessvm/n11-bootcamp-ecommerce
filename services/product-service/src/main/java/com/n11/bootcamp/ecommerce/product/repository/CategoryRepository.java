package com.n11.bootcamp.ecommerce.product.repository;

import com.n11.bootcamp.ecommerce.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}