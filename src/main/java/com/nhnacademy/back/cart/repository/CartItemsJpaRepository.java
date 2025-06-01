package com.nhnacademy.back.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.cart.domain.entity.Cart;
import com.nhnacademy.back.cart.domain.entity.CartItems;
import com.nhnacademy.back.product.product.domain.entity.Product;

public interface CartItemsJpaRepository extends JpaRepository<CartItems, Long> {
	/// 장바구니에 상품이 이미 존재하는지 확인 메소드
	boolean existsByCartAndProduct(Cart cart, Product product);

	/// 특정 고객의 cartItems 목록 조회 메소드
	List<CartItems> findByCart_Customer_CustomerId(long customerId);

	/// 특정 장바구니 및 상품이 해당하는 장바구니 항목 조회
	Optional<CartItems> findByCartAndProduct(Cart cart, Product product);

}
