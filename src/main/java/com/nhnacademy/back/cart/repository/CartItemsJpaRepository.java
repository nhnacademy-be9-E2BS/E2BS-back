package com.nhnacademy.back.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.cart.domain.entity.Cart;
import com.nhnacademy.back.cart.domain.entity.CartItems;

public interface CartItemsJpaRepository extends JpaRepository<CartItems, Long> {
	/// 장바구니를 통해 항목 전체 삭제 메소드
	void deleteCartItemsByCart(Cart cart);
}
