package com.nhnacademy.back.order.order.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.OrderAdminService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderAdminServiceImpl implements OrderAdminService {
	private final OrderJpaRepository orderJpaRepository;

	/**
	 * 페이지를 받으면 해당하는 주문 리스트를 반환하는 메서드
	 * 최근 날짜 순으로 보여줌
	 */
	@Override
	public Page<ResponseOrderDTO> getOrders(Pageable pageable) {
		return orderJpaRepository.findAllByOrderByOrderCreatedAtDesc(pageable).map(ResponseOrderDTO::fromEntity);
	}
}
