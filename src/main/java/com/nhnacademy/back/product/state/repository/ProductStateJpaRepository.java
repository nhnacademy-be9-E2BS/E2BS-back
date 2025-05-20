package com.nhnacademy.back.product.state.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

public interface ProductStateJpaRepository extends JpaRepository<ProductState, Long> {
	ProductState findByProductStateName(ProductStateName productStateName);
}