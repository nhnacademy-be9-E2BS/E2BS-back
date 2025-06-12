package com.nhnacademy.back.batch;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.batch.order.OrderDeleteScheduler;
import com.nhnacademy.back.order.order.model.entity.Order;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderDeleteSchedulerTest {
	@Mock
	OrderJpaRepository orderJpaRepository;

	@Mock
	OrderService orderService;

	@InjectMocks
	OrderDeleteScheduler orderDeleteScheduler;

	@Test
	void deleteUnpaidOrders_success() {
		// given
		Order order1 = mock(Order.class);
		Order order2 = mock(Order.class);

		when(order1.getOrderCode()).thenReturn("ORDER_001");
		when(order2.getOrderCode()).thenReturn("ORDER_002");

		when(orderJpaRepository.findByOrderPaymentStatusIsFalseAndOrderCreatedAtBefore(any()))
			.thenReturn(List.of(order1, order2));

		// when
		orderDeleteScheduler.deleteUnpaidOrders();

		// Then
		verify(orderJpaRepository, times(1))
			.findByOrderPaymentStatusIsFalseAndOrderCreatedAtBefore(any());

		verify(orderService, times(1)).deleteOrder("ORDER_001");
		verify(orderService, times(1)).deleteOrder("ORDER_002");
	}
}
