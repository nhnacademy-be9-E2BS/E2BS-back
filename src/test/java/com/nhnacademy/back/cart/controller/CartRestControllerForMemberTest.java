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
import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForMemberDTO;
import com.nhnacademy.back.cart.service.CartService;

@WebMvcTest(controllers = CartRestController.class)
class CartRestControllerForMemberTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CartService cartService;

	@Autowired
	private ObjectMapper objectMapper;


	@Test
	@DisplayName("POST /api/members/carts/items - 회원 장바구니 항목 추가 테스트")
	void createCartItemForMember() throws Exception {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO("id123", "", 1L, 3);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/api/members/carts/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isCreated());

		verify(cartService).createCartItemForMember(any(RequestAddCartItemsDTO.class));
	}

	@Test
	@DisplayName("PUT /api/members/carts/items/{cartItemsId} - 회원 장바구니 항목 수량 변경 테스트")
	void updateCartItemForMember() throws Exception {
		// given
		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO(null, 1L, 5);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/members/carts/items/{cartItemsId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isNoContent());

		verify(cartService).updateCartItemForMember(anyLong(), any(RequestUpdateCartItemsDTO.class));
	}

	@Test
	@DisplayName("DELETE /api/members/carts/items/{cartItemsId} - 회원 장바구니 항목 삭제 테스트")
	void deleteCartItemForMember() throws Exception {
		// when & then
		mockMvc.perform(delete("/api/members/carts/items/{cartItemsId}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		verify(cartService).deleteCartItemForMember(1L);
	}

	@Test
	@DisplayName("DELETE /api/members/{memberId}/carts - 회원 장바구니 항목 전체 삭제 테스트")
	void deleteCartForMember() throws Exception {
		// when & then
		mockMvc.perform(delete("/api/members/{memberId}/carts", "id123")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		verify(cartService).deleteCartForMember("id123");
	}

	@Test
	@DisplayName("GET /api/members/{memberId}/carts - 회원 장바구니 목록 조회 테스트")
	void getCartItemsByMember() throws Exception {
		// given
		List<ResponseCartItemsForMemberDTO> cartItems = List.of(
			new ResponseCartItemsForMemberDTO(1L, 100L, List.of(), "Product A", 1000, "path/image.jpg", 2, 2000)
		);

		when(cartService.getCartItemsByMember("id123")).thenReturn(cartItems);

		// when & then
		mockMvc.perform(get("/api/members/{memberId}/carts", "id123"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[0].productId").value(100L))
			.andExpect(jsonPath("$.[0].productTitle").value("Product A"))
			.andExpect(jsonPath("$.[0].productSalePrice").value(1000))
			.andExpect(jsonPath("$.[0].productImagePath").value("path/image.jpg"))
			.andExpect(jsonPath("$.[0].cartItemsQuantity").value(2));
	}

}
