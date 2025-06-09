package com.nhnacademy.back.order.order;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.order.order.controller.OrderMemberController;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderDetailDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderReturnDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderReturnDTO;
import com.nhnacademy.back.order.order.service.OrderService;
import com.nhnacademy.back.order.orderreturn.service.OrderReturnService;
import com.nhnacademy.back.order.payment.service.PaymentService;

@WebMvcTest(OrderMemberController.class)
@ActiveProfiles("dev")
class OrderMemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;

	@MockitoBean
	private PaymentService paymentService;

	@MockitoBean
	private OrderReturnService orderReturnService;

	@Autowired
	private ObjectMapper objectMapper;

	private RequestOrderDTO getTestOrderDTO() {
		RequestOrderDTO orderDTO = new RequestOrderDTO();
		orderDTO.setDeliveryFeeId(1L);
		orderDTO.setCustomerId(100L);
		orderDTO.setOrderReceiverName("테스트");
		orderDTO.setOrderReceiverPhone("01012345678");
		orderDTO.setOrderAddressCode("12345");
		orderDTO.setOrderAddressInfo("서울특별시 강남구 도산대로");
		orderDTO.setOrderAddressExtra("도곡빌딩 3층");
		orderDTO.setOrderPointAmount(5000L);
		orderDTO.setOrderPaymentStatus(true);
		orderDTO.setOrderPaymentAmount(25000L);
		return orderDTO;
	}

	private RequestOrderDetailDTO getTestOrderDetailDTO() {
		RequestOrderDetailDTO orderDetailDTO = new RequestOrderDetailDTO();
		orderDetailDTO.setProductId(10L);
		orderDetailDTO.setOrderCode("TEST-ORDER-CODE");
		orderDetailDTO.setWrapperId(123L);
		orderDetailDTO.setOrderQuantity(3);
		orderDetailDTO.setOrderDetailPerPrice(8900L);
		return orderDetailDTO;
	}


	@Test
	@DisplayName("포인트 주문 생성 테스트")
	void testCreatePointOrder() throws Exception {
		RequestOrderWrapperDTO request = new RequestOrderWrapperDTO(getTestOrderDTO(),
			new ArrayList<RequestOrderDetailDTO>(Arrays.asList(getTestOrderDetailDTO())));
		ResponseOrderResultDTO responseDTO = new ResponseOrderResultDTO();
		when(orderService.createPointOrder(any()))
			.thenReturn(ResponseEntity.ok(responseDTO));

		mockMvc.perform(post("/api/auth/orders/create/point")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("포인트 주문 생성 테스트(검증 실패)")
	void testCreatePointOrder_validationFail() throws Exception {
		RequestOrderWrapperDTO request = new RequestOrderWrapperDTO(getTestOrderDTO(),
			new ArrayList<RequestOrderDetailDTO>(new ArrayList<>()));

		mockMvc.perform(post("/api/auth/orders/create/point")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(result -> assertThat(result.getResolvedException())
				.isInstanceOf(ValidationFailedException.class));
	}

	@Test
	@DisplayName("회원의 주문 내역 조회 테스트")
	void testGetOrders() throws Exception {
		// given
		List<ResponseOrderDTO> content = List.of(new ResponseOrderDTO());
		Page<ResponseOrderDTO> page = new PageImpl<>(content);
		when(orderService.getOrdersByMemberId(any(Pageable.class), anyString(), any(), any(), any(), any())).thenReturn(
			page);

		// when & then
		mockMvc.perform(get("/api/auth/orders")
				.param("page", "0")
				.param("size", "10")
				.param("memberId", "memberId"))
			.andExpect(status().isOk());

		verify(orderService).getOrdersByMemberId(any(Pageable.class), anyString(), any(), any(), any(), any());
	}

	@Test
	@DisplayName("회원의 주문 내역 조회 테스트(날짜 검색 시)")
	void testGetOrders_dateSearch() throws Exception {
		// given
		List<ResponseOrderDTO> content = List.of(new ResponseOrderDTO());
		Page<ResponseOrderDTO> page = new PageImpl<>(content);
		when(orderService.getOrdersByMemberId(any(Pageable.class), anyString(), any(), any(), any(), any())).thenReturn(
			page);

		// when & then
		mockMvc.perform(get("/api/auth/orders")
				.param("startDate", "2020-01-01")
				.param("endDate", "2020-12-31")
				.param("page", "0")
				.param("size", "10")
				.param("memberId", "memberId"))
			.andExpect(status().isOk());

		verify(orderService).getOrdersByMemberId(any(Pageable.class), anyString(), any(), any(), any(), any());
	}

	@Test
	@DisplayName("회원의 주문 취소 요청 테스트")
	void testCancelOrder() throws Exception {
		ResponseEntity<Void> response = ResponseEntity.ok().build();
		String orderCode = "TEST-ORDER-CODE";

		when(orderService.cancelOrder(orderCode)).thenReturn(response);

		mockMvc.perform(delete("/api/auth/orders/" + orderCode))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("회원의 반품 요청 테스트")
	void testReturnOrder() throws Exception {
		String orderCode = "TEST-ORDER-CODE";
		RequestOrderReturnDTO returnDTO = new RequestOrderReturnDTO(orderCode, "BREAK", "BREAK");
		ResponseEntity<Void> response = ResponseEntity.ok().build();

		when(orderService.returnOrder(returnDTO)).thenReturn(response);

		mockMvc.perform(post("/api/auth/orders/return")
				.content(objectMapper.writeValueAsString(returnDTO))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("회원의 반품 요청 테스트(검증 실패)")
	void testReturnOrder_validationFail() throws Exception {
		RequestOrderReturnDTO returnDTO = new RequestOrderReturnDTO(null, null, null);

		mockMvc.perform(post("/api/auth/orders/return")
				.content(objectMapper.writeValueAsString(returnDTO))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(result -> assertThat(result.getResolvedException())
				.isInstanceOf(ValidationFailedException.class));
	}

	@Test
	@DisplayName("회원의 반품 목록 조회 테스트")
	void testGetReturnOrders() throws Exception {
		Page<ResponseOrderReturnDTO> pageDTO = new PageImpl<>(List.of(new ResponseOrderReturnDTO()));
		ResponseEntity<Page<ResponseOrderReturnDTO>> response = ResponseEntity.ok(pageDTO);
		when(orderReturnService.getOrderReturnsByMemberId(anyString(), any())).thenReturn(response);

		mockMvc.perform(get("/api/auth/orders/return")
				.param("memberId", "memberId")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("회원의 반품 상세 내역 조회 테스트")
	void testGetReturnOrderByOrderCode() throws Exception {
		String orderCode = "TEST-ORDER-CODE";
		ResponseEntity<ResponseOrderReturnDTO> response = ResponseEntity.ok(new ResponseOrderReturnDTO());
		when(orderReturnService.getOrderReturnByOrderCode(anyString())).thenReturn(response);

		mockMvc.perform(get("/api/auth/orders/return/" + orderCode))
			.andExpect(status().isOk());
	}

}
