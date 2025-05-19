package com.nhnacademy.back.order.order;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.order.order.controller.OrderController;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderDetailDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseTossPaymentConfirmDTO;
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
		orderDetailDTO.setOrderStateId(1L);
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

		mockMvc.perform(post("/api/order/create/tossPay")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("토스 결제 주문 생성 테스트 - 잘못된 요청")
	void testCreateOrderFail() throws Exception {
		RequestOrderWrapperDTO request = new RequestOrderWrapperDTO();

		mockMvc.perform(post("/api/order/create/tossPay")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(result -> assertThat(result.getResolvedException())
				.isInstanceOf(ValidationFailedException.class));
	}

	@Test
	@DisplayName("포인트 주문 생성 테스트")
	void testCreatePointOrder() throws Exception {
		RequestOrderWrapperDTO request = new RequestOrderWrapperDTO(getTestOrderDTO(),
			new ArrayList<RequestOrderDetailDTO>(Arrays.asList(getTestOrderDetailDTO())));
		ResponseOrderResultDTO responseDTO = new ResponseOrderResultDTO();
		when(orderService.createPointOrder(any()))
			.thenReturn(ResponseEntity.ok(responseDTO));

		mockMvc.perform(post("/api/order/create/point")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("포인트 주문 생성 테스트 - 잘못된 요청")
	void testCreatePointOrderFail() throws Exception {
		RequestOrderWrapperDTO request = new RequestOrderWrapperDTO();
		mockMvc.perform(post("/api/order/create/point")
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

		ResponseTossPaymentConfirmDTO confirmDTO = new ResponseTossPaymentConfirmDTO();
		when(orderService.confirmOrder(eq(orderId), eq(paymentKey), eq(amount)))
			.thenReturn(ResponseEntity.ok(confirmDTO));

		mockMvc.perform(post("/api/order/confirm")
				.param("orderId", orderId)
				.param("paymentKey", paymentKey)
				.param("amount", String.valueOf(amount)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("결제 승인 실패 시 주문 취소 및 상태코드 반환 테스트")
	void testOrderConfirmFailure() throws Exception {
		String orderId = "TEST-ORDER-CODE";
		String paymentKey = "TEST-PAYMENT-KEY";
		long amount = 10000L;

		ResponseEntity<ResponseTossPaymentConfirmDTO> failedResponse =
			ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		Mockito.when(orderService.confirmOrder(eq(orderId), eq(paymentKey), eq(amount)))
			.thenReturn(failedResponse);

		Mockito.when(orderService.cancelOrder(eq(orderId)))
			.thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(post("/api/order/confirm")
				.param("orderId", orderId)
				.param("paymentKey", paymentKey)
				.param("amount", String.valueOf(amount)))
			.andExpect(status().isBadRequest());

		Mockito.verify(orderService).cancelOrder(eq(orderId));
	}

	@Test
	@DisplayName("주문 취소 테스트")
	void testCancelOrder() throws Exception {
		String orderId = "orderToCancel";

		when(orderService.cancelOrder(eq(orderId)))
			.thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(post("/api/order/cancel")
				.param("orderId", orderId))
			.andExpect(status().isOk());
	}
}
