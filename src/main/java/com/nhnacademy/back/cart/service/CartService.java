package com.nhnacademy.back.cart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.cart.domain.dto.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestDeleteCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsDTO;

public interface CartService {
	void createCartItem(RequestAddCartItemsDTO request);
	void updateCartItem(long cartItemId, RequestUpdateCartItemsDTO request);
	void deleteCartItem(long cartItemId, RequestDeleteCartItemsDTO request);

	Page<ResponseCartItemsDTO> getCartItemsByCustomer(long customerId, Pageable pageable);
}
