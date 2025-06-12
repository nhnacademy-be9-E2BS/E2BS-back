package com.nhnacademy.back.order.order.adaptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.order.order.adaptor.mapper.TossResponseMapper;
import com.nhnacademy.back.order.order.model.dto.request.RequestCancelDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestPaymentApproveDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestTossConfirmDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponsePaymentConfirmDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseTossPaymentConfirmDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TossPaymentAdaptor implements PaymentAdaptor {
	private final TossAdaptor tossAdaptor;
	private final TossResponseMapper tossMapper;
	@Value("${order.sc}")
	private String secretKey;

	@Override
	public String getName() {
		return "TOSS";
	}

	@Override
	public ResponseEntity<ResponsePaymentConfirmDTO> confirmOrder(RequestPaymentApproveDTO request) {
		RequestTossConfirmDTO dto = new RequestTossConfirmDTO(
			request.getOrderId(), request.getPaymentKey(), request.getAmount());

		ResponseEntity<ResponseTossPaymentConfirmDTO> response = tossAdaptor.confirmOrder(dto, secretKey);

		return ResponseEntity.status(response.getStatusCode()).body(tossMapper.toResult(response.getBody()));
	}

	@Override
	public ResponseEntity<Void> cancelOrder(String paymentKey, RequestCancelDTO request) {
		//현재 토스의 결제 취소 DTO를 공통 DTO라 가정, 별개 공통 DTO 생성 시 Mapper를 만들어야 함
		ResponseEntity<ResponseTossPaymentConfirmDTO> dto = tossAdaptor.cancelOrder(paymentKey, request, secretKey);
		return ResponseEntity.status(dto.getStatusCode()).build();
	}
}
