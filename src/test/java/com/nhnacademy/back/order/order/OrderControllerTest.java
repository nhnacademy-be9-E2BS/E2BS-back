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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.order.order.controller.OrderController;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderDetailDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestPaymentApproveDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderWrapperDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponsePaymentConfirmDTO;
import com.nhnacademy.back.order.order.service.OrderService;
import com.nhnacademy.back.order.payment.service.PaymentService;

@WebMvcTest(OrderController.class)
@ActiveProfiles("dev")
class OrderControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;

	@MockitoBean
	private PaymentService paymentService;

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
	@DisplayName("토스 결제 주문 생성 테스트")
	void testCreateOrder() throws Exception {
		RequestOrderWrapperDTO request = new RequestOrderWrapperDTO(getTestOrderDTO(),
			new ArrayList<RequestOrderDetailDTO>(Arrays.asList(getTestOrderDetailDTO())));
		ResponseOrderResultDTO responseDTO = new ResponseOrderResultDTO();
		when(orderService.createOrder(any()))
			.thenReturn(ResponseEntity.ok(responseDTO));

		mockMvc.perform(post("/api/orders/create/payment")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("토스 결제 주문 생성 테스트 - 잘못된 요청")
	void testCreateOrderFail() throws Exception {
		RequestOrderWrapperDTO request = new RequestOrderWrapperDTO();

		mockMvc.perform(post("/api/orders/create/payment")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(result -> assertThat(result.getResolvedException())
				.isInstanceOf(ValidationFailedException.class));
	}

	@Test
	@DisplayName("결제 승인 테스트")
	void testOrderConfirm() throws Exception {
		String orderId = "TEST-ORDER-CODE";
		String paymentKey = "TEST-PAYMENT-KEY";
		long amount = 1000L;

		RequestPaymentApproveDTO approveDTO = new RequestPaymentApproveDTO(orderId, paymentKey, amount, "TOSS");
		ResponseEntity<ResponsePaymentConfirmDTO> response = ResponseEntity.ok(new ResponsePaymentConfirmDTO());
		when(orderService.confirmOrder(any())).thenReturn(response);

		mockMvc.perform(post("/api/orders/confirm")
				.content(objectMapper.writeValueAsString(approveDTO))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("결제 승인 실패 시 주문서 삭제 및 상태코드 반환 테스트")
	void testOrderConfirmFailure() throws Exception {
		String orderId = "TEST-ORDER-CODE";
		String paymentKey = "TEST-PAYMENT-KEY";
		long amount = 10000L;
		RequestPaymentApproveDTO approveDTO = new RequestPaymentApproveDTO(orderId, paymentKey, amount, "TOSS");

		ResponseEntity<ResponsePaymentConfirmDTO> failedResponse =
			ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		when(orderService.confirmOrder(any())).thenReturn(failedResponse);

		when(orderService.deleteOrder(orderId))
			.thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(post("/api/orders/confirm")
				.content(objectMapper.writeValueAsString(approveDTO))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());

		verify(orderService).deleteOrder(orderId);
	}

	@Test
	@DisplayName("주문 삭제 테스트")
	void testDeleteOrder() throws Exception {
		String orderId = "TEST-ORDER-CODE";

		when(orderService.deleteOrder(orderId))
			.thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(post("/api/orders/cancel")
				.param("orderId", orderId))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("주문 상세 정보 조회 테스트")
	void testGetOrder() throws Exception {
		String orderId = "TEST-ORDER-CODE";
		ResponseOrderWrapperDTO response = new ResponseOrderWrapperDTO();

		when(orderService.getOrderByOrderCode(orderId)).thenReturn(response);

		mockMvc.perform(get("/api/orders/" + orderId))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("비회원의 주문 내역 조회 테스트")
	void testGetOrders() throws Exception {
		// given
		List<ResponseOrderDTO> content = List.of(new ResponseOrderDTO());
		Page<ResponseOrderDTO> page = new PageImpl<>(content);
		when(orderService.getOrdersByCustomerId(any(Pageable.class), anyLong())).thenReturn(page);

		// when & then
		mockMvc.perform(get("/api/orders/customers/orders")
				.param("page", "0")
				.param("size", "10")
				.param("customerId", "1"))
			.andExpect(status().isOk());

		verify(orderService).getOrdersByCustomerId(any(Pageable.class), anyLong());
	}

}
