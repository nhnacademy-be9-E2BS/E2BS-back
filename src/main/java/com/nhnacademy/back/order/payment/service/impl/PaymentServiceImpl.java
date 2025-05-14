package com.nhnacademy.back.order.payment.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.nhnacademy.back.order.order.domain.dto.response.ResponseTossPaymentConfirmDTO;
import com.nhnacademy.back.order.order.domain.entity.Order;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.payment.domain.entity.Payment;
import com.nhnacademy.back.order.payment.repository.PaymentJpaRepository;
import com.nhnacademy.back.order.payment.service.PaymentService;
import com.nhnacademy.back.order.paymentmethod.domain.entity.PaymentMethod;
import com.nhnacademy.back.order.paymentmethod.repository.PaymentMethodJpaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentJpaRepository paymentJpaRepository;
	private final OrderJpaRepository orderJpaRepository;
	private final PaymentMethodJpaRepository paymentMethodJpaRepository;

	@Override
	public void createPayment(ResponseTossPaymentConfirmDTO responseTossPaymentConfirmDTO) {
		String orderId = responseTossPaymentConfirmDTO.getOrderId();
		String paymentKey = responseTossPaymentConfirmDTO.getPaymentKey();
		long totalAmount = responseTossPaymentConfirmDTO.getTotalAmount();
		LocalDateTime paymentRequestedAt = responseTossPaymentConfirmDTO.getRequestedAt().toLocalDateTime();
		LocalDateTime paymentApprovedAt = responseTossPaymentConfirmDTO.getApprovedAt().toLocalDateTime();

		Order order = orderJpaRepository.findById(orderId).orElse(null);
		// Toss가 1번
		PaymentMethod paymentMethod = paymentMethodJpaRepository.findById(Long.valueOf(1)).orElse(null);

		Payment payment = new Payment(order, paymentMethod, paymentKey, totalAmount, paymentRequestedAt,
			paymentApprovedAt);
		paymentJpaRepository.save(payment);
	}
}
