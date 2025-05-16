package com.nhnacademy.back.cart.service;

import java.util.List;

import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForCustomerDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForGuestDTO;

public interface CartService {
	void createCartItemForCustomer(RequestAddCartItemsDTO request);
	void updateCartItemForCustomer(long cartItemId, RequestUpdateCartItemsDTO request);
	void deleteCartItemForCustomer(long cartItemId);
	void deleteCartForCustomer(long customerId);
	List<ResponseCartItemsForCustomerDTO> getCartItemsByCustomer(long customerId);

	void createCartItemForGuest(RequestAddCartItemsDTO request);
	void updateCartItemForGuest(RequestUpdateCartItemsDTO request);
	void deleteCartItemForGuest(RequestDeleteCartItemsForGuestDTO request);
	void deleteCartForGuest(String sessionId);
	List<ResponseCartItemsForGuestDTO> getCartItemsByGuest(String sessionId);
}
