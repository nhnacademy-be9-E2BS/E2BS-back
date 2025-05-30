package com.nhnacademy.back.cart.service;

import java.util.List;

import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForMemberDTO;

public interface CartService {
	int createCartItemForMember(RequestAddCartItemsDTO request);
	int updateCartItemForMember(long cartItemId, RequestUpdateCartItemsDTO request);
	void deleteCartItemForMember(long cartItemId);
	void deleteCartForMember(String memberId);
	List<ResponseCartItemsForMemberDTO> getCartItemsByMember(String memberId);
	Integer getCartItemsCountsForMember(String memberId);

	int createCartItemForGuest(RequestAddCartItemsDTO request);
	int updateCartItemForGuest(RequestUpdateCartItemsDTO request);
	void deleteCartItemForGuest(RequestDeleteCartItemsForGuestDTO request);
	void deleteCartForGuest(String sessionId);
	List<ResponseCartItemsForGuestDTO> getCartItemsByGuest(String sessionId);
	Integer getCartItemsCountsForGuest(String sessionId);
}
