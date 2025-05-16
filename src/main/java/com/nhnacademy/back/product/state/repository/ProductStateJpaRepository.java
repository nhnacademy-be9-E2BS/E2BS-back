package com.nhnacademy.back.product.state.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.state.domain.entity.ProductState;

public interface ProductStateJpaRepository extends JpaRepository<ProductState, Long> {
}