package com.nhnacademy.back.order.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nhnacademy.back.order.order.adaptor.TossAdaptor;
import com.nhnacademy.back.order.order.adaptor.TossPaymentAdaptor;
import com.nhnacademy.back.order.order.adaptor.mapper.TossResponseMapper;
import com.nhnacademy.back.order.order.model.dto.request.RequestPaymentApproveDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponsePaymentConfirmDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseTossPaymentConfirmDTO;

class TossPaymentAdaptorTest {
	TossAdaptor tossAdaptor;
	TossResponseMapper tossMapper;
	TossPaymentAdaptor tossPaymentAdaptor;

	final String secretKey = "test-secret-key";

	@BeforeEach
	void setUp() {
		tossAdaptor = mock(TossAdaptor.class);
		tossMapper = mock(TossResponseMapper.class);

		tossPaymentAdaptor = new TossPaymentAdaptor(tossAdaptor, tossMapper);
		// secretKey를 @Value가 아니라 리플렉션으로 주입
		injectSecretKey(tossPaymentAdaptor, secretKey);
	}

	@Test
	@DisplayName("토스 어뎁터 이름 정상 반환")
	void testGetName_returnsToss() {
		assertEquals("TOSS", tossPaymentAdaptor.getName());
	}

	@Test
	@DisplayName("토스 어뎁터의 결제 승인 요청")
	void testConfirmOrder_returnsMappedResponse() {
		// given
		RequestPaymentApproveDTO request = new RequestPaymentApproveDTO("order-001", "key-123", 1000L, "TOSS");

		ResponseTossPaymentConfirmDTO tossResponse = new ResponseTossPaymentConfirmDTO();
		ResponsePaymentConfirmDTO mappedResult = new ResponsePaymentConfirmDTO();

		when(tossAdaptor.confirmOrder(any(), any()))
			.thenReturn(new ResponseEntity<>(tossResponse, HttpStatus.OK));
		when(tossMapper.toResult(tossResponse)).thenReturn(mappedResult);

		// when
		ResponseEntity<ResponsePaymentConfirmDTO> result = tossPaymentAdaptor.confirmOrder(request);

		// then
		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(mappedResult, result.getBody());
		verify(tossAdaptor).confirmOrder(any(), any());
		verify(tossMapper).toResult(tossResponse);
	}

	// Reflection으로 private 필드에 secretKey 넣는 유틸
	private void injectSecretKey(TossPaymentAdaptor adaptor, String key) {
		try {
			java.lang.reflect.Field field = TossPaymentAdaptor.class.getDeclaredField("secretKey");
			field.setAccessible(true);
			field.set(adaptor, key);
		} catch (Exception e) {
			throw new RuntimeException("Failed to inject secretKey", e);
		}
	}
}
