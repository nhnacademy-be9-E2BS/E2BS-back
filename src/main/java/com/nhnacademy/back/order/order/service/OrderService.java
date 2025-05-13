package com.nhnacademy.back.order.order.service;

import org.springframework.http.ResponseEntity;

import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderResultDTO;

public interface OrderService {
	ResponseEntity<ResponseOrderResultDTO> CreateOrder(RequestOrderWrapperDTO requestOrderWrapperDTO);

	ResponseEntity<Void> confirmOrder(String orderId, String paymentKey, long amount);
}
