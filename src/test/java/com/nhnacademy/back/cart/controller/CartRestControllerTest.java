package com.nhnacademy.back.cart.controller;



import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.cart.domain.dto.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestDeleteCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsDTO;
import com.nhnacademy.back.cart.service.CartService;

@WebMvcTest(controllers = CartRestController.class)
public class CartRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CartService cartService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@DisplayName("POST /api/carts/items - 비회원/회원 장바구니 항목 추가 테스트")
	void createCartItem() throws Exception {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(1L, "", 1L, 3);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/api/carts/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isCreated());

		verify(cartService).createCartItem(any(RequestAddCartItemsDTO.class));
	}

	@Test
	@DisplayName("PUT /api/carts/items/{cartItemsId} - 장바구니 항목 수량 변경 테스트")
	void updateCartItem() throws Exception {
		// given
		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO(null, 5);
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(put("/api/carts/items/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isNoContent());

		verify(cartService).updateCartItem(eq(1L), any(RequestUpdateCartItemsDTO.class));
	}

	@Test
	@DisplayName("DELETE /api/carts/items/{cartItemsId} - 장바구니 항목 삭제 테스트")
	void testDeleteCartItem() throws Exception {
		// given
		RequestDeleteCartItemsDTO request = new RequestDeleteCartItemsDTO("");
		String jsonRequest = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(delete("/api/carts/items/1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonRequest))
			.andExpect(status().isNoContent());

		verify(cartService).deleteCartItem(eq(1L), any(RequestDeleteCartItemsDTO.class));
	}

	@Test
	@DisplayName("GET /api/customers/{customerId}/carts - 고객 장바구니 목록 조회 테스트")
	void testGetCartItemsByCustomer() throws Exception {
		// given
		List<ResponseCartItemsDTO> cartItems = List.of(
			new ResponseCartItemsDTO(1L, 100L, "Product A", 1000, "path/image.jpg", 2)
		);
		Page<ResponseCartItemsDTO> page = new PageImpl<>(cartItems);

		when(cartService.getCartItemsByCustomer(eq(1L), any(Pageable.class))).thenReturn(page);

		// when & then
		mockMvc.perform(get("/api/customers/1/carts"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].productId").value(100L))
			.andExpect(jsonPath("$.content[0].productTitle").value("Product A"))
			.andExpect(jsonPath("$.content[0].productSalePrice").value(1000))
			.andExpect(jsonPath("$.content[0].productImagePath").value("path/image.jpg"))
			.andExpect(jsonPath("$.content[0].cartItemsQuantity").value(2));
	}
}
