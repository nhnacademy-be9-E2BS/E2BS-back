package com.nhnacademy.back.order.order.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.model.entity.Order;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.OrderAdminService;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderStateName;
import com.nhnacademy.back.order.orderstate.repository.OrderStateJpaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderAdminServiceImpl implements OrderAdminService {
	private final OrderJpaRepository orderJpaRepository;
	private final OrderDetailJpaRepository orderDetailJpaRepository;
	private final OrderStateJpaRepository orderStateJpaRepository;

	/**
	 * 페이지를 받으면 해당하는 주문 리스트를 반환하는 메서드
	 * 최근 날짜 순으로 보여줌
	 */
	@Override
	public Page<ResponseOrderDTO> getOrders(Pageable pageable, String stateName, LocalDate startDate, LocalDate endDate,
		String orderCode, String memberId) {
		if (stateName != null && !stateName.isEmpty()) { // 주문 상태 검색
			OrderState orderState = orderStateJpaRepository.findByOrderStateName(OrderStateName.valueOf(stateName))
				.orElse(null);
			return orderJpaRepository.findAllByOrderStateOrderByOrderCreatedAtDesc(pageable, orderState)
				.map(ResponseOrderDTO::fromEntity);
		} else if (startDate != null && endDate != null) { // 날짜 검색
			return orderJpaRepository
				.findAllByOrderCreatedAtBetweenOrderByOrderCreatedAtDesc(pageable, startDate.atStartOfDay(),
					endDate.atTime(LocalTime.MAX))
				.map(ResponseOrderDTO::fromEntity);
		} else if (orderCode != null && !orderCode.isEmpty()) { // 주문 코드로 검색
			return orderJpaRepository.searchByOrderCodeIgnoreCase(orderCode, pageable)
				.map(ResponseOrderDTO::fromEntity);
		} else if (memberId != null && !memberId.isEmpty()) { // 멤버 id 검색
			return orderJpaRepository.searchByMemberIdIgnoreCase(memberId, pageable).map(ResponseOrderDTO::fromEntity);
		} else { // 전체 검색
			return orderJpaRepository.findAllByOrderByOrderCreatedAtDesc(pageable).map(ResponseOrderDTO::fromEntity);
		}
	}

	/**
	 * 특정 주문 코드의 주문 상태를 배송 시작으로 바꾸는 메서드
	 */
	@Override
	@Transactional
	public ResponseEntity<Void> startDelivery(String orderCode) {
		Order order = orderJpaRepository.findById(orderCode).orElseThrow();
		OrderState orderState = orderStateJpaRepository.findByOrderStateName(OrderStateName.DELIVERY).orElse(null);
		order.updateOrderState(orderState);
		order.updateOrderShipmentDate(LocalDate.now());
		return ResponseEntity.ok().build();
	}

}
