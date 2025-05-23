package com.nhnacademy.back.order.order.service.impl;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.domain.entity.Order;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.OrderAdminService;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderStateName;
import com.nhnacademy.back.order.orderstate.repository.OrderStateJpaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderAdminServiceImpl implements OrderAdminService {
	private final OrderJpaRepository orderJpaRepository;
	private final OrderDetailJpaRepository orderDetailJpaRepository;
	private final OrderStateJpaRepository orderStateJpaRepository;

	/**
	 * 페이지를 받으면 해당하는 주문 리스트를 반환하는 메서드
	 * 최근 날짜 순으로 보여줌
	 */
	@Override
	public Page<ResponseOrderDTO> getOrders(Pageable pageable) {
		return orderJpaRepository.findAllByOrderByOrderCreatedAtDesc(pageable).map(ResponseOrderDTO::fromEntity);
	}

	/**
	 * 페이지와 주문 상태를 매개변수로 받아서 해당하는 주문 목록을 반환하는 메서드
	 */
	@Override
	public Page<ResponseOrderDTO> getOrders(Pageable pageable, Long stateId) {
		return orderJpaRepository.findAllByOrderState_OrderStateIdOrderByOrderCreatedAtDesc(pageable, stateId)
			.map(ResponseOrderDTO::fromEntity);
	}

	/**
	 * 특정 주문 코드의 주문 상태를 배송 시작으로 바꾸는 메서드
	 */
	@Override
	public ResponseEntity<Void> startDelivery(String orderCode) {
		Order order = orderJpaRepository.findById(orderCode).orElseThrow();
		OrderState orderState = orderStateJpaRepository.findByOrderStateName(OrderStateName.DELIVERY).orElse(null);
		order.updateOrderState(orderState);
		order.updateOrderShipmentDate(LocalDate.now());
		orderJpaRepository.save(order);
		return ResponseEntity.ok().build();
	}

}
