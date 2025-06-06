package com.nhnacademy.back.order.orderreturn.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderReturnDTO;
import com.nhnacademy.back.order.orderreturn.domain.entity.OrderReturn;
import com.nhnacademy.back.order.orderreturn.repository.OrderReturnJpaRepository;
import com.nhnacademy.back.order.orderreturn.service.OrderReturnService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderReturnServiceImpl implements OrderReturnService {
	private final OrderReturnJpaRepository orderReturnJpaRepository;
	private final MemberJpaRepository memberJpaRepository;

	@Override
	public ResponseEntity<Page<ResponseOrderReturnDTO>> getOrderReturnsByMemberId(String memberId, Pageable pageable) {
		Customer customer = memberJpaRepository.getMemberByMemberId(memberId).getCustomer();
		Page<ResponseOrderReturnDTO> orderReturns =
			orderReturnJpaRepository.findByOrder_CustomerOrderByOrderReturnCreatedAtDesc(customer, pageable)
				.map(ResponseOrderReturnDTO::fromEntity);
		return ResponseEntity.ok(orderReturns);
	}

	@Override
	public ResponseEntity<ResponseOrderReturnDTO> getOrderReturnByOrderCode(String orderCode) {
		OrderReturn orderReturn = orderReturnJpaRepository.findByOrder_OrderCode(orderCode).orElseThrow();
		ResponseOrderReturnDTO responseOrderReturnDTO = ResponseOrderReturnDTO.fromEntity(orderReturn);
		return ResponseEntity.ok(responseOrderReturnDTO);
	}

	@Override
	public ResponseEntity<Page<ResponseOrderReturnDTO>> getOrderReturnsAll(Pageable pageable) {
		Page<ResponseOrderReturnDTO> orderReturns =
			orderReturnJpaRepository.findAllByOrderByOrderReturnCreatedAtDesc(pageable)
				.map(ResponseOrderReturnDTO::fromEntity);
		return ResponseEntity.ok(orderReturns);
	}
}
