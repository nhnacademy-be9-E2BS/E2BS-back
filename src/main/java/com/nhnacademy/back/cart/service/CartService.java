package com.nhnacademy.back.cart.service;

import java.util.List;

import com.nhnacademy.back.cart.domain.dto.CartItemDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForMemberDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartOrderDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForMemberDTO;

public interface CartService {
	void createCartForMember(String memberId);
	int createCartItemForMember(RequestAddCartItemsDTO request);
	int updateCartItemForMember(RequestUpdateCartItemsDTO request);
	void deleteCartItemForMember(RequestDeleteCartItemsForMemberDTO request);
	void deleteCartForMember(String memberId);
	List<ResponseCartItemsForMemberDTO> getCartItemsByMember(String memberId);
	Integer getCartItemsCountsForMember(String memberId);

	int createCartItemForGuest(RequestAddCartItemsDTO request);
	int updateCartItemForGuest(RequestUpdateCartItemsDTO request);
	void deleteCartItemForGuest(RequestDeleteCartItemsForGuestDTO request);
	void deleteCartForGuest(String sessionId);
	List<ResponseCartItemsForGuestDTO> getCartItemsByGuest(String sessionId);

	Integer mergeCartItemsToMemberFromGuest(String memberId, String sessionId);

	Integer deleteOrderCompleteCartItems(RequestDeleteCartOrderDTO requestOrderCartDeleteDTO);

	void saveCartItemsDBFromRedis(String memberId, List<CartItemDTO> cartItems);
}
