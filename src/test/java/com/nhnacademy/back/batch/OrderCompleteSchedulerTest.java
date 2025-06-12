package com.nhnacademy.back.batch;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.batch.order.OrderCompleteScheduler;
import com.nhnacademy.back.order.order.model.entity.Order;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderStateName;
import com.nhnacademy.back.order.orderstate.repository.OrderStateJpaRepository;

@ExtendWith(MockitoExtension.class)
class OrderCompleteSchedulerTest {

	@Mock
	private OrderJpaRepository orderJpaRepository;

	@Mock
	private OrderStateJpaRepository orderStateJpaRepository;

	@InjectMocks
	private OrderCompleteScheduler scheduler;

	@Test
	void updateShipmentToCompleted_success() {
		// Given
		OrderState deliveryState = mock(OrderState.class);
		OrderState completeState = mock(OrderState.class);

		Order order1 = mock(Order.class);
		Order order2 = mock(Order.class);
		when(order1.getOrderCode()).thenReturn("ORD001");
		when(order2.getOrderCode()).thenReturn("ORD002");

		when(orderStateJpaRepository.findByOrderStateName(OrderStateName.DELIVERY))
			.thenReturn(Optional.of(deliveryState));
		when(orderStateJpaRepository.findByOrderStateName(OrderStateName.COMPLETE))
			.thenReturn(Optional.of(completeState));
		when(orderJpaRepository.findAllByOrderState_OrderStateIdAndOrderShipmentDateBefore(
			eq(deliveryState.getOrderStateId()), any(LocalDate.class)))
			.thenReturn(List.of(order1, order2));

		// When
		scheduler.updateShipmentToCompleted();

		// Then
		verify(order1).updateOrderState(completeState);
		verify(order2).updateOrderState(completeState);
		verify(orderJpaRepository).findAllByOrderState_OrderStateIdAndOrderShipmentDateBefore(
			eq(deliveryState.getOrderStateId()), any(LocalDate.class));
	}
}
