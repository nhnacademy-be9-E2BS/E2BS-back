package com.nhnacademy.back.order.payment.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.nhnacademy.back.order.order.model.dto.response.ResponseTossPaymentConfirmDTO;
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
	public void createPayment(ResponseTossPaymentConfirmDTO responseTossPaymentConfirmDTO) {
		String orderId = responseTossPaymentConfirmDTO.getOrderId();
		String paymentKey = responseTossPaymentConfirmDTO.getPaymentKey();
		long totalAmount = responseTossPaymentConfirmDTO.getTotalAmount();
		LocalDateTime paymentRequestedAt = responseTossPaymentConfirmDTO.getRequestedAt().toLocalDateTime();
		LocalDateTime paymentApprovedAt = responseTossPaymentConfirmDTO.getApprovedAt().toLocalDateTime();

		Order order = orderJpaRepository.findById(orderId).orElse(null);
		PaymentMethod paymentMethod = null;

		// 결제 방식 추가 시 PaymentMethodName에 추가 후 밑의 if문 추가(토스 공식 문서 형식 참조 바람)
		if (responseTossPaymentConfirmDTO.getMethod().equals("간편결제")) {
			if (responseTossPaymentConfirmDTO.getEasyPay().getProvider().equals("토스페이")) {
				//토스 간편 결제
				paymentMethod = paymentMethodJpaRepository.findByPaymentMethodName(PaymentMethodName.TOSS).orElse(null);
			}
		} else if (responseTossPaymentConfirmDTO.getMethod().equals("휴대폰")) {
			// 휴대폰 결제
			paymentMethod = paymentMethodJpaRepository.findByPaymentMethodName(PaymentMethodName.PHONE).orElse(null);
		} else {
			// 이외 결제 방식
			paymentMethod = paymentMethodJpaRepository.findByPaymentMethodName(PaymentMethodName.OTHER).orElse(null);
		}

		Payment payment = new Payment(order, paymentMethod, paymentKey, totalAmount, paymentRequestedAt,
			paymentApprovedAt);
		paymentJpaRepository.save(payment);
	}
}
