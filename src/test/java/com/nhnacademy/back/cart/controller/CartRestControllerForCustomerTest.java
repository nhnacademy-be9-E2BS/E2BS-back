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
import com.nhnacademy.back.cart.domain.dto.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsForCustomerDTO;
import com.nhnacademy.back.cart.service.CartService;

@WebMvcTest(controllers = CartRestController.class)
class CartRestControllerForCustomerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CartService cartService;

	private final ObjectMapper objectMapper = new ObjectMapper();


	@Test
	@DisplayName("POST /api/customers/carts/items - 비회원/회원 장바구니 항목 추가 테스트")
	void createCartItemForCustomer() throws Exception {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(1L, "", 1L, 3);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/api/customers/carts/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isCreated());

		verify(cartService).createCartItemForCustomer(any(RequestAddCartItemsDTO.class));
	}

	@Test
	@DisplayName("PUT /api/customers/carts/items/{cartItemsId} - 장바구니 항목 수량 변경 테스트")
	void updateCartItemForCustomer() throws Exception {
		// given
		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO(null, null, 5);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/customers/carts/items/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isNoContent());

		verify(cartService).updateCartItemForCustomer(eq(1L), any(RequestUpdateCartItemsDTO.class));
	}

	@Test
	@DisplayName("DELETE /api/customers/carts/items/{cartItemsId} - 장바구니 항목 삭제 테스트")
	void deleteCartItemForCustomer() throws Exception {
		// when & then
		mockMvc.perform(delete("/api/customers/carts/items/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		verify(cartService).deleteCartItemForCustomer(eq(1L));
	}

	@Test
	@DisplayName("DELETE /api/customers/{customerId}/carts - 장바구니 항목 전체 삭제 테스트")
	void deleteCartForCustomer() throws Exception {
		// when & then
		mockMvc.perform(delete("/api/customers/1/carts")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		verify(cartService).deleteCartForCustomer(eq(1L));
	}

	@Test
	@DisplayName("GET /api/customers/{customerId}/carts - 고객 장바구니 목록 조회 테스트")
	void getCartItemsByCustomer() throws Exception {
		// given
		List<ResponseCartItemsForCustomerDTO> cartItems = List.of(
			new ResponseCartItemsForCustomerDTO(1L, 100L, List.of(), "Product A", 1000, "path/image.jpg", 2, 2000)
		);

		when(cartService.getCartItemsByCustomer(eq(1L))).thenReturn(cartItems);

		// when & then
		mockMvc.perform(get("/api/customers/1/carts"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[0].productId").value(100L))
			.andExpect(jsonPath("$.[0].productTitle").value("Product A"))
			.andExpect(jsonPath("$.[0].productSalePrice").value(1000))
			.andExpect(jsonPath("$.[0].productImagePath").value("path/image.jpg"))
			.andExpect(jsonPath("$.[0].cartItemsQuantity").value(2));
	}

}
