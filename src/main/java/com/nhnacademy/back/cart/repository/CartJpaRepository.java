package com.nhnacademy.back.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.cart.domain.entity.Cart;

public interface CartJpaRepository extends JpaRepository<Cart, Long> {
	/// 해당 고객의 장바구니 조회 메소드
	Optional<Cart> findByCustomer_CustomerId(long customerId);
}
