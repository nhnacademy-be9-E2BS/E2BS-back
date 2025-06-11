package com.nhnacademy.back.order.payment;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.order.order.model.dto.response.ResponsePaymentConfirmDTO;
import com.nhnacademy.back.order.order.model.entity.Order;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.payment.repository.PaymentJpaRepository;
import com.nhnacademy.back.order.payment.service.impl.PaymentServiceImpl;
import com.nhnacademy.back.order.paymentmethod.domain.entity.PaymentMethod;
import com.nhnacademy.back.order.paymentmethod.repository.PaymentMethodJpaRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
	@InjectMocks
	private PaymentServiceImpl paymentService;

	@Mock
	private PaymentJpaRepository paymentJpaRepository;
	@Mock
	private OrderJpaRepository orderJpaRepository;
	@Mock
	private PaymentMethodJpaRepository paymentMethodJpaRepository;

	@Test
	@DisplayName("토스 결제 정보 저장")
	void testCreatePayment_success() {
		// given
		String orderId = "TEST-ORDER-CODE";
		String paymentKey = "TEST-PAYMENT-KEY";
		long totalAmount = 10000;
		LocalDateTime requestedAt = mock(LocalDateTime.class);
		LocalDateTime approvedAt = mock(LocalDateTime.class);

		// 모의 DTO
		ResponsePaymentConfirmDTO dto = mock(ResponsePaymentConfirmDTO.class);
		when(dto.getOrderId()).thenReturn(orderId);
		when(dto.getPaymentKey()).thenReturn(paymentKey);
		when(dto.getTotalAmount()).thenReturn(totalAmount);
		when(dto.getRequestedAt()).thenReturn(requestedAt);
		when(dto.getApprovedAt()).thenReturn(approvedAt);
		when(dto.getProvider()).thenReturn("TOSS");

		// 모의 Order, PaymentMethod
		Order order = mock(Order.class);
		PaymentMethod paymentMethod = mock(PaymentMethod.class);

		when(orderJpaRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(paymentMethodJpaRepository.findByPaymentMethodName(any())).thenReturn(Optional.of(paymentMethod));

		// when
		paymentService.createPayment(dto);

		// then
		verify(paymentJpaRepository, times(1)).save(any());
	}

}
