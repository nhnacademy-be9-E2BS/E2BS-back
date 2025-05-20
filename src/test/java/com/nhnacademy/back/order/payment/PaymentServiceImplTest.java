package com.nhnacademy.back.order.payment;

import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.order.order.domain.dto.response.ResponseTossPaymentConfirmDTO;
import com.nhnacademy.back.order.order.domain.entity.Order;
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
		int totalAmount = 10000;
		ZonedDateTime requestedAt = mock(ZonedDateTime.class);
		ZonedDateTime approvedAt = mock(ZonedDateTime.class);

		// 모의 DTO
		ResponseTossPaymentConfirmDTO dto = mock(ResponseTossPaymentConfirmDTO.class);
		when(dto.getOrderId()).thenReturn(orderId);
		when(dto.getPaymentKey()).thenReturn(paymentKey);
		when(dto.getTotalAmount()).thenReturn(totalAmount);
		when(dto.getRequestedAt()).thenReturn(requestedAt);
		when(dto.getApprovedAt()).thenReturn(approvedAt);

		// 모의 Order, PaymentMethod
		Order order = mock(Order.class);
		PaymentMethod paymentMethod = mock(PaymentMethod.class);

		when(orderJpaRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(paymentMethodJpaRepository.findById(1L)).thenReturn(Optional.of(paymentMethod));

		// when
		paymentService.createPayment(dto);

		// then
		verify(paymentJpaRepository, times(1)).save(any());
	}

}
