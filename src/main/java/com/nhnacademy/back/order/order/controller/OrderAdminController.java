package com.nhnacademy.back.order.order.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderReturnDTO;
import com.nhnacademy.back.order.order.service.OrderAdminService;
import com.nhnacademy.back.order.orderreturn.service.OrderReturnService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/admin/orders")
public class OrderAdminController {
	private final OrderAdminService orderAdminService;
	private final OrderReturnService orderReturnService;

	/**
	 * 전체 주문 내역 목록을 주문 상태에 따라 반환하는 메서드
	 */
	@Admin
	@GetMapping
	public ResponseEntity<Page<ResponseOrderDTO>> getOrders(Pageable pageable,
		@RequestParam(required = false) String stateName,
		@RequestParam(required = false) LocalDate startDate,
		@RequestParam(required = false) LocalDate endDate,
		@RequestParam(required = false) String orderCode,
		@RequestParam(required = false) String memberId) {

		return ResponseEntity.ok(
			orderAdminService.getOrders(pageable, stateName, startDate, endDate, orderCode, memberId));

	}

	/**
	 * 특정 주문 코드의 주문 상태를 배송 시작으로 바꾸는 메서드
	 */
	@Admin
	@PostMapping("/{orderCode}")
	public ResponseEntity<Void> startDelivery(@PathVariable String orderCode) {
		return orderAdminService.startDelivery(orderCode);
	}

	@Admin
	@GetMapping("/return")
	public ResponseEntity<Page<ResponseOrderReturnDTO>> getOrderReturns(Pageable pageable) {
		return orderReturnService.getOrderReturnsAll(pageable);
	}

}
