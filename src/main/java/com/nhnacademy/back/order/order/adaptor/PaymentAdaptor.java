package com.nhnacademy.back.order.order.adaptor;

import org.springframework.http.ResponseEntity;

import com.nhnacademy.back.order.order.model.dto.request.RequestCancelDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestPaymentApproveDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponsePaymentConfirmDTO;

public interface PaymentAdaptor {
	String getName();

	ResponseEntity<ResponsePaymentConfirmDTO> confirmOrder(RequestPaymentApproveDTO request);

	ResponseEntity<Void> cancelOrder(String paymentKey,RequestCancelDTO request);
}
