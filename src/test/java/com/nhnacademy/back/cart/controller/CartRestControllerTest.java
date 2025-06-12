package com.nhnacademy.back.cart.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartOrderDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestMergeCartItemDTO;
import com.nhnacademy.back.cart.service.CartService;

@WebMvcTest(CartRestController.class)
class CartRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CartService cartService;


	@Test
	@DisplayName("GET /api/carts/counts - 장바구니 상품 수 조회 테스트")
	void getCartItemsCounts_success() throws Exception {
		// given
		String memberId = "user123";
		when(cartService.getCartItemsCountsForMember(memberId)).thenReturn(5);

		// when & then
		mockMvc.perform(get("/api/carts/counts")
				.param("memberId", memberId))
			.andExpect(status().isOk())
			.andExpect(content().string("5"));

		verify(cartService).getCartItemsCountsForMember(memberId);
	}

	@Test
	@DisplayName("POST /api/carts/merge - 비회원 장바구니 병합 테스트")
	void mergeCartItemsToMemberFromGuest_success() throws Exception {
		// given
		RequestMergeCartItemDTO dto = new RequestMergeCartItemDTO("member123", "session456");
		String jsonRequest = objectMapper.writeValueAsString(dto);

		when(cartService.mergeCartItemsToMemberFromGuest(dto.getMemberId(), dto.getSessionId()))
			.thenReturn(3);

		// when & then
		mockMvc.perform(post("/api/carts/merge")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isOk())
			.andExpect(content().string("3"));

		verify(cartService).mergeCartItemsToMemberFromGuest(dto.getMemberId(), dto.getSessionId());
	}

	@Test
	@DisplayName("POST /api/carts/orders - 주문한 장바구니 항목 삭제 테스트")
	void deleteOrderCompleteCartItems_success() throws Exception {
		// given
		RequestDeleteCartOrderDTO dto = new RequestDeleteCartOrderDTO();
		dto.setMemberId("user789");
		dto.setCartQuantities(List.of(1, 2, 3));
		String jsonRequest = objectMapper.writeValueAsString(dto);

		when(cartService.deleteOrderCompleteCartItems(dto)).thenReturn(3);

		// when & then
		mockMvc.perform(post("/api/carts/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isOk())
			.andExpect(content().string("3"));

		verify(cartService).deleteOrderCompleteCartItems(dto);
	}

}
