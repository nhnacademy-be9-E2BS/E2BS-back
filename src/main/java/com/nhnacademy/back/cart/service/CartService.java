package com.nhnacademy.back.cart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.cart.domain.dto.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsForCustomerDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsForGuestDTO;

public interface CartService {
	void createCartItemForCustomer(RequestAddCartItemsDTO request);
	void updateCartItemForCustomer(long cartItemId, RequestUpdateCartItemsDTO request);
	void deleteCartItemForCustomer(long cartItemId);
	void deleteCartForCustomer(long customerId);
	Page<ResponseCartItemsForCustomerDTO> getCartItemsByCustomer(long customerId, Pageable pageable);

	void createCartItemForGuest(RequestAddCartItemsDTO request);
	void updateCartItemForGuest(RequestUpdateCartItemsDTO request);
	void deleteCartItemForGuest(RequestDeleteCartItemsForGuestDTO request);
	void deleteCartForGuest(String sessionId);
	Page<ResponseCartItemsForGuestDTO> getCartItemsByGuest(String sessionId, Pageable pageable);
}
