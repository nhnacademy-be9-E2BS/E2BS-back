package com.nhnacademy.back.order.order.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.back.order.order.model.dto.response.ResponseMemberOrderDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseMemberRecentOrderDTO;
import com.nhnacademy.back.order.order.service.OrderService;

@WebMvcTest(controllers = OrderMypageController.class)
class OrderMypageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;

	@Test
	@DisplayName("마이페이지 회원 총 주문 건수 조회 테스트")
	void getMemberOrdersTest() throws Exception {

		// Given
		ResponseMemberOrderDTO responseMemberOrderDTO = new ResponseMemberOrderDTO(
			"memberId", 1
		);

		// When
		when(orderService.getMemberOrdersCnt("user")).thenReturn(responseMemberOrderDTO);

		// Then
		mockMvc.perform(get("/api/auth/mypage/user/orders/counts"))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("마이페이지 회원 최근 주문한 제품 조회 테스트")
	void getMemberRecentOrdersTest() throws Exception {

		// Given
		ResponseMemberRecentOrderDTO responseMemberRecentOrderDTO = new ResponseMemberRecentOrderDTO(
			LocalDateTime.now(), "123456", new ArrayList<>(), "DELIVERY"
		);

		// When
		when(orderService.getMemberRecentOrders("user"))
			.thenReturn(List.of(responseMemberRecentOrderDTO));

		// Then
		mockMvc.perform(get("/api/auth/mypage/user/orders"))
			.andExpect(status().isCreated());

	}

}