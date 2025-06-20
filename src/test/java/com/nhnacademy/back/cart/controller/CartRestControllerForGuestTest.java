package com.nhnacademy.back.cart.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForGuestDTO;
import com.nhnacademy.back.cart.service.CartService;

@WebMvcTest(controllers = CartRestController.class)
class CartRestControllerForGuestTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CartService cartService;

	@Autowired
	private ObjectMapper objectMapper;

	
	@Test
	@DisplayName("POST /api/guests/carts/items - 게스트 장바구니 항목 추가 테스트")
	void createCartItemForGuest() throws Exception {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(null, "session123", 1L, 2);
		String jsonRequest = objectMapper.writeValueAsString(request);

		when(cartService.createCartItemForGuest(any(RequestAddCartItemsDTO.class))).thenReturn(2);

		// when & then
		mockMvc.perform(post("/api/guests/carts/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isCreated())
			.andExpect(content().string("2"));

		verify(cartService).createCartItemForGuest(any(RequestAddCartItemsDTO.class));
	}

	@Test
	@DisplayName("POST /api/guests/carts/items - 게스트 장바구니 항목 추가 테스트 실패(유효성 검증)")
	void createCartItemForGuest_Fail_Validation() throws Exception {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(null, null, null, null);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/api/guests/carts/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PUT /api/guests/carts/items - 게스트 장바구니 항목 수량 변경 테스트")
	void updateCartItemForGuest() throws Exception {
		// given
		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO("", "session123", 1L,3);
		String jsonRequest = objectMapper.writeValueAsString(request);

		when(cartService.updateCartItemForGuest(any(RequestUpdateCartItemsDTO.class))).thenReturn(3);

		// when & then
		mockMvc.perform(put("/api/guests/carts/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isOk())
			.andExpect(content().string("3"));

		verify(cartService).updateCartItemForGuest(any(RequestUpdateCartItemsDTO.class));
	}

	@Test
	@DisplayName("PUT /api/guests/carts/items - 게스트 장바구니 항목 수량 변경 테스트 실패(유효성 검증)")
	void updateCartItemForGuest_Fail_Validation() throws Exception {
		// given
		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO(null, null, null, null);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/guests/carts/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("DELETE /api/guests/carts/items - 게스트 장바구니 항목 삭제 테스트")
	void deleteCartItemForGuest() throws Exception {
		// given
		RequestDeleteCartItemsForGuestDTO request = new RequestDeleteCartItemsForGuestDTO("session123", 1L);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(delete("/api/guests/carts/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isNoContent());

		verify(cartService).deleteCartItemForGuest(any(RequestDeleteCartItemsForGuestDTO.class));
	}

	@Test
	@DisplayName("DELETE /api/guests/carts/items - 게스트 장바구니 항목 삭제 테스트 실패(유효성 검증)")
	void deleteCartItemForGuest_Fail_Validation() throws Exception {
		// given
		RequestDeleteCartItemsForGuestDTO request = new RequestDeleteCartItemsForGuestDTO(null, null);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(delete("/api/guests/carts/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("DELETE /api/guests/{sessionId}/carts - 게스트 장바구니 항목 전체 삭제 테스트")
	void deleteCartForGuest() throws Exception {
		String sessionId = "sessionId-123";

		mockMvc.perform(delete("/api/guests/{sessionId}/carts", sessionId))
			.andExpect(status().isNoContent());

		verify(cartService).deleteCartForGuest(sessionId);
	}

	@Test
	@DisplayName("GET /api/guests/{sessionId}/carts/- 게스트 장바구니 목록 조회 테스트")
	void getCartItemsByGuest() throws Exception {
		// given
		String sessionId = "session123";
		List<ResponseCartItemsForGuestDTO> cartItems = List.of(
			new ResponseCartItemsForGuestDTO(1L, "Product 1", 1000, 500, new BigDecimal(50), "/image1.jpg", 2, 1000)
		);

		when(cartService.getCartItemsByGuest(sessionId)).thenReturn(cartItems);

		// when & then
		mockMvc.perform(get("/api/guests/{sessionId}/carts", sessionId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[0].productId").value(1L))
			.andExpect(jsonPath("$.[0].productTitle").value("Product 1"));

		verify(cartService).getCartItemsByGuest(sessionId);
	}

}
