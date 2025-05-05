package com.nhnacademy.back.product.status.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.status.domain.entity.ProductStatus;

public interface ProductStatusJpaRepository extends JpaRepository<ProductStatus, Long> {
}
