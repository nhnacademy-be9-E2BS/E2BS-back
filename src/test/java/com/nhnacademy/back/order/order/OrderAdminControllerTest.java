package com.nhnacademy.back.order.order;

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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.order.order.controller.OrderAdminController;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.service.OrderAdminService;
import com.nhnacademy.back.order.orderreturn.service.OrderReturnService;

@WebMvcTest(OrderAdminController.class)
@ActiveProfiles("dev")
class OrderAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderAdminService orderAdminService;

	@MockitoBean
	private OrderReturnService orderReturnService;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("전체 주문 목록 조회(필터링 없음)")
	void testGetOrdersWithoutStateId() throws Exception {
		// given
		List<ResponseOrderDTO> content = List.of(new ResponseOrderDTO());
		Page<ResponseOrderDTO> page = new PageImpl<>(content);
		when(orderAdminService.getOrders(any(Pageable.class))).thenReturn(page);

		// when & then
		mockMvc.perform(get("/api/auth/admin/orders")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk());

		verify(orderAdminService).getOrders(any(Pageable.class));
	}

	@Test
	@DisplayName("전체 주문 목록 조회(필터링 있음)")
	void testGetOrdersWithStateId() throws Exception {
		// given
		List<ResponseOrderDTO> content = List.of(new ResponseOrderDTO());
		Page<ResponseOrderDTO> page = new PageImpl<>(content);
		when(orderAdminService.getOrders(any(Pageable.class), eq(1L))).thenReturn(page);

		// when & then
		mockMvc.perform(get("/api/auth/admin/orders")
				.param("page", "0")
				.param("size", "10")
				.param("stateId", "1"))
			.andExpect(status().isOk());

		verify(orderAdminService).getOrders(any(Pageable.class), eq(1L));
	}

	@Test
	@DisplayName("주문 상태 배송 시작 변경")
	void testStartDelivery() throws Exception {
		// given
		String orderCode = "TEST-ORDER-CODE";
		when(orderAdminService.startDelivery(orderCode)).thenReturn(ResponseEntity.ok().build());

		// when & then
		mockMvc.perform(post("/api/auth/admin/orders/{orderCode}", orderCode))
			.andExpect(status().isOk());

		verify(orderAdminService).startDelivery(orderCode);
	}
}
