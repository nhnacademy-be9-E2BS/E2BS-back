package com.nhnacademy.back.order.order.service;

import org.springframework.http.ResponseEntity;

import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseTossPaymentConfirmDTO;

public interface OrderService {
	ResponseEntity<ResponseOrderResultDTO> createOrder(RequestOrderWrapperDTO requestOrderWrapperDTO);

	ResponseEntity<ResponseOrderResultDTO> createPointOrder(RequestOrderWrapperDTO requestOrderWrapperDTO);

	ResponseEntity<ResponseTossPaymentConfirmDTO> confirmOrder(String orderId, String paymentKey, long amount);

	ResponseEntity<Void> cancelOrder(String orderId);

	ResponseOrderWrapperDTO getOrderByOrderCode(String orderCode);
}
