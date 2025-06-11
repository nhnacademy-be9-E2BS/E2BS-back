package com.nhnacademy.back.order.payment.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.nhnacademy.back.order.order.model.dto.response.ResponsePaymentConfirmDTO;
import com.nhnacademy.back.order.order.model.entity.Order;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.payment.domain.entity.Payment;
import com.nhnacademy.back.order.payment.repository.PaymentJpaRepository;
import com.nhnacademy.back.order.payment.service.PaymentService;
import com.nhnacademy.back.order.paymentmethod.domain.entity.PaymentMethod;
import com.nhnacademy.back.order.paymentmethod.domain.entity.PaymentMethodName;
import com.nhnacademy.back.order.paymentmethod.repository.PaymentMethodJpaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentJpaRepository paymentJpaRepository;
	private final OrderJpaRepository orderJpaRepository;
	private final PaymentMethodJpaRepository paymentMethodJpaRepository;

	@Override
	public void createPayment(ResponsePaymentConfirmDTO paymentResponse) {
		String orderId = paymentResponse.getOrderId();
		String paymentKey = paymentResponse.getPaymentKey();
		long totalAmount = paymentResponse.getTotalAmount();
		LocalDateTime paymentRequestedAt = paymentResponse.getRequestedAt();
		LocalDateTime paymentApprovedAt = paymentResponse.getApprovedAt();

		Order order = orderJpaRepository.findById(orderId).orElse(null);

		// 프로바이더에 따라 결제 수단을 추가
		PaymentMethod paymentMethod = paymentMethodJpaRepository
			.findByPaymentMethodName(PaymentMethodName.fromProvider(paymentResponse.getProvider())).orElse(null);

		Payment payment = new Payment(order, paymentMethod, paymentKey, totalAmount, paymentRequestedAt,
			paymentApprovedAt);
		paymentJpaRepository.save(payment);
	}
}
