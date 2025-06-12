package com.nhnacademy.back.order.order;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nhnacademy.back.order.order.adaptor.mapper.TossResponseMapper;
import com.nhnacademy.back.order.order.model.dto.response.ResponsePaymentConfirmDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseTossPaymentConfirmDTO;

class TossResponseMapperTest {

	TossResponseMapper mapper = new TossResponseMapper();

	@Test
	@DisplayName("토스 응답 공통 응답 변환")
	void testToResult_mapsAllFieldsCorrectly() {
		// given
		LocalDateTime now = LocalDateTime.now();
		ZonedDateTime requestedAt = ZonedDateTime.of(now.minusMinutes(5), ZoneOffset.UTC);
		ZonedDateTime approvedAt = ZonedDateTime.of(now, ZoneOffset.UTC);

		ResponseTossPaymentConfirmDTO dto = new ResponseTossPaymentConfirmDTO();
		dto.setOrderId("ORDER-777");
		dto.setPaymentKey("PK-987");
		dto.setTotalAmount(10000);
		dto.setRequestedAt(requestedAt);
		dto.setApprovedAt(approvedAt);

		// when
		ResponsePaymentConfirmDTO result = mapper.toResult(dto);

		// then
		assertEquals("ORDER-777", result.getOrderId());
		assertEquals("PK-987", result.getPaymentKey());
		assertEquals(10000, result.getTotalAmount());
		assertEquals("TOSS", result.getProvider());
		assertEquals(requestedAt.toLocalDateTime(), result.getRequestedAt());
		assertEquals(approvedAt.toLocalDateTime(), result.getApprovedAt());
	}
}
