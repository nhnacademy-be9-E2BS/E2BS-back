package com.nhnacademy.back.order.order.adaptor.mapper;

import org.springframework.stereotype.Component;

import com.nhnacademy.back.order.order.model.dto.response.ResponsePaymentConfirmDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseTossPaymentConfirmDTO;

@Component
public class TossResponseMapper {

	public ResponsePaymentConfirmDTO toResult(ResponseTossPaymentConfirmDTO dto) {
		return ResponsePaymentConfirmDTO.builder()
			.orderId(dto.getOrderId())
			.paymentKey(dto.getPaymentKey())
			.totalAmount(dto.getTotalAmount())
			.provider("TOSS")
			.requestedAt(dto.getRequestedAt().toLocalDateTime())
			.approvedAt(dto.getApprovedAt().toLocalDateTime())
			.build();
	}
}