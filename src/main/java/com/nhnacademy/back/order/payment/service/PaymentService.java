package com.nhnacademy.back.order.payment.service;

import com.nhnacademy.back.order.order.model.dto.response.ResponseTossPaymentConfirmDTO;

public interface PaymentService {

	void createPayment(ResponseTossPaymentConfirmDTO responseTossPaymentConfirmDTO);
}
