package com.nhnacademy.back.order.order;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.model.entity.Order;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.impl.OrderAdminServiceImpl;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderStateName;
import com.nhnacademy.back.order.orderstate.repository.OrderStateJpaRepository;

@ExtendWith(MockitoExtension.class)
class OrderAdminServiceImplTest {
	@Mock
	private OrderJpaRepository orderJpaRepository;

	@Mock
	private OrderStateJpaRepository orderStateJpaRepository;

	@InjectMocks
	private OrderAdminServiceImpl orderAdminService;

	@Test
	@DisplayName("주문 목록 전체 조회")
	void testGetOrders() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Order order = mock(Order.class);
		ResponseOrderDTO responseOrderDTO = mock(ResponseOrderDTO.class);
		Page<Order> orderPage = new PageImpl<>(List.of(order));

		when(orderJpaRepository.findAllByOrderByOrderCreatedAtDesc(pageable)).thenReturn(orderPage);
		try (MockedStatic<ResponseOrderDTO> mockedStatic = Mockito.mockStatic(ResponseOrderDTO.class)) {
			mockedStatic.when(() -> ResponseOrderDTO.fromEntity(order)).thenReturn(responseOrderDTO);

			// when
			Page<ResponseOrderDTO> result = orderAdminService.getOrders(pageable, null, null, null, null, null);

			// then
			assertThat(result).isNotNull();
			assertEquals(1, result.getContent().size());
			verify(orderJpaRepository).findAllByOrderByOrderCreatedAtDesc(pageable);
		}
	}

	@Test
	@DisplayName("주문 목록 주문 상태 필터링 조회")
	void testGetOrdersWithState() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		OrderState state = mock(OrderState.class);
		Order order = mock(Order.class);
		ResponseOrderDTO responseOrderDTO = mock(ResponseOrderDTO.class);
		Page<Order> orderPage = new PageImpl<>(List.of(order));

		when(orderStateJpaRepository.findByOrderStateName(any())).thenReturn(Optional.of(state));
		when(orderJpaRepository.findAllByOrderStateOrderByOrderCreatedAtDesc(pageable, state))
			.thenReturn(orderPage);
		try (MockedStatic<ResponseOrderDTO> mockedStatic = Mockito.mockStatic(ResponseOrderDTO.class)) {
			mockedStatic.when(() -> ResponseOrderDTO.fromEntity(order)).thenReturn(responseOrderDTO);

			// when
			Page<ResponseOrderDTO> result = orderAdminService.getOrders(pageable, "WAIT", null, null, null, null);

			// then
			assertThat(result).isNotNull();
			assertEquals(1, result.getContent().size());
			verify(orderJpaRepository).findAllByOrderStateOrderByOrderCreatedAtDesc(pageable, state);
		}
	}

	@Test
	@DisplayName("주문 목록 주문 날짜 필터링 조회")
	void testGetOrdersWithDate() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusDays(1);
		Order order = mock(Order.class);
		ResponseOrderDTO responseOrderDTO = mock(ResponseOrderDTO.class);
		Page<Order> orderPage = new PageImpl<>(List.of(order));

		when(orderJpaRepository.findAllByOrderCreatedAtBetweenOrderByOrderCreatedAtDesc(pageable,
			startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))).thenReturn(orderPage);

		try (MockedStatic<ResponseOrderDTO> mockedStatic = Mockito.mockStatic(ResponseOrderDTO.class)) {
			mockedStatic.when(() -> ResponseOrderDTO.fromEntity(order)).thenReturn(responseOrderDTO);

			// when
			Page<ResponseOrderDTO> result = orderAdminService.getOrders(pageable, null, startDate, endDate, null, null);

			// then
			assertThat(result).isNotNull();
			assertEquals(1, result.getContent().size());
			verify(orderJpaRepository).findAllByOrderCreatedAtBetweenOrderByOrderCreatedAtDesc(pageable,
				startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
		}
	}

	@Test
	@DisplayName("주문 목록 주문 코드 필터링 조회")
	void testGetOrdersWithOrderCode() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Order order = mock(Order.class);
		ResponseOrderDTO responseOrderDTO = mock(ResponseOrderDTO.class);
		Page<Order> orderPage = new PageImpl<>(List.of(order));

		when(orderJpaRepository.searchByOrderCodeIgnoreCase("TEST-ORDER-CODE", pageable)).thenReturn(orderPage);

		try (MockedStatic<ResponseOrderDTO> mockedStatic = Mockito.mockStatic(ResponseOrderDTO.class)) {
			mockedStatic.when(() -> ResponseOrderDTO.fromEntity(order)).thenReturn(responseOrderDTO);

			// when
			Page<ResponseOrderDTO> result = orderAdminService.getOrders(pageable, null, null, null, "TEST-ORDER-CODE",
				null);

			// then
			assertThat(result).isNotNull();
			assertEquals(1, result.getContent().size());
			verify(orderJpaRepository).searchByOrderCodeIgnoreCase("TEST-ORDER-CODE", pageable);
		}
	}

	@Test
	@DisplayName("주문 목록 회원 ID 필터링 조회")
	void testGetOrdersWithMemberId() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Order order = mock(Order.class);
		ResponseOrderDTO responseOrderDTO = mock(ResponseOrderDTO.class);
		Page<Order> orderPage = new PageImpl<>(List.of(order));

		when(orderJpaRepository.searchByMemberIdIgnoreCase("MemberId", pageable)).thenReturn(orderPage);

		try (MockedStatic<ResponseOrderDTO> mockedStatic = Mockito.mockStatic(ResponseOrderDTO.class)) {
			mockedStatic.when(() -> ResponseOrderDTO.fromEntity(order)).thenReturn(responseOrderDTO);

			// when
			Page<ResponseOrderDTO> result = orderAdminService.getOrders(pageable, null, null, null, null, "MemberId");

			// then
			assertThat(result).isNotNull();
			assertEquals(1, result.getContent().size());
			verify(orderJpaRepository).searchByMemberIdIgnoreCase("MemberId", pageable);
		}
	}

	@Test
	@DisplayName("특정 주문의 주문 상태 배송 중으로 변경")
	void testStartDelivery() {
		// given
		String orderCode = "TEST-ORDER-CODE";
		Order mockOrder = mock(Order.class);
		OrderState deliveryState = mock(OrderState.class);

		when(orderJpaRepository.findById(orderCode)).thenReturn(Optional.of(mockOrder));
		when(orderStateJpaRepository.findByOrderStateName(OrderStateName.DELIVERY)).thenReturn(
			Optional.of(deliveryState));

		// when
		ResponseEntity<Void> response = orderAdminService.startDelivery(orderCode);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(mockOrder).updateOrderState(deliveryState);
	}
}
