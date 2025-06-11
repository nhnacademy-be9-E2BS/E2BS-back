package com.nhnacademy.back.order.payment.service;

import com.nhnacademy.back.order.order.model.dto.response.ResponsePaymentConfirmDTO;

public interface PaymentService {
	void createPayment(ResponsePaymentConfirmDTO responseTossPaymentConfirmDTO);
}
