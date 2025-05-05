package com.nhnacademy.back.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.cart.domain.entity.Cart;

public interface CartJpaRepository extends JpaRepository<Cart, Long> {
}
