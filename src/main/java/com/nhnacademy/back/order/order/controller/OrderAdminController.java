package com.nhnacademy.back.order.order.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderReturnDTO;
import com.nhnacademy.back.order.order.service.OrderAdminService;
import com.nhnacademy.back.order.orderreturn.service.OrderReturnService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자 주문 API", description = "관리자의 주문 관련 기능 제공")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/admin/orders")
public class OrderAdminController {
	private final OrderAdminService orderAdminService;
	private final OrderReturnService orderReturnService;

	/**
	 * 전체 주문 내역 목록을 주문 상태에 따라 반환하는 메서드
	 */
	@Operation(summary = "주문 목록 조회", description = "관리자가 주문 내역 목록을 필터링 조건에 따라 조회할 수 있는 기능")
	@Admin
	@GetMapping
	public ResponseEntity<Page<ResponseOrderDTO>> getOrders(Pageable pageable,
		@Parameter(description = "주문 상태") @RequestParam(required = false) String stateName,
		@Parameter(description = "주문 일자 시작 일") @RequestParam(required = false) String startDate,
		@Parameter(description = "주문 일자 끝 일") @RequestParam(required = false) String endDate,
		@Parameter(description = "주문 코드") @RequestParam(required = false) String orderCode,
		@Parameter(description = "회원 ID") @RequestParam(required = false) String memberId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// prod 환경에서만 LocalDate가 이상하게 주고 받아 직접 문자열을 전송한다.
		LocalDate start = null;
		LocalDate end = null;

		if (startDate != null && !startDate.isEmpty()) {
			start = LocalDate.parse(startDate, formatter);
		}
		if (endDate != null && !endDate.isEmpty()) {
			end = LocalDate.parse(endDate, formatter);
		}

		return ResponseEntity.ok(
			orderAdminService.getOrders(pageable, stateName, start, end, orderCode, memberId));

	}

	/**
	 * 특정 주문 코드의 주문 상태를 배송 시작으로 바꾸는 메서드
	 */
	@Operation(summary = "주문 배송 시작", description = "관리자가 배송 대기 상태의 주문을 배송 중 상태로 바꾸는 기능")
	@Admin
	@PostMapping("/{order-code}")
	public ResponseEntity<Void> startDelivery(@Parameter(description = "주문 코드") @PathVariable(name = "order-code") String orderCode) {
		return orderAdminService.startDelivery(orderCode);
	}

	@Operation(summary = "반품 내역 목록 조회", description = "관리자가 전체 반품 목록에 대해 조회하는 기능")
	@Admin
	@GetMapping("/return")
	public ResponseEntity<Page<ResponseOrderReturnDTO>> getOrderReturns(Pageable pageable) {
		return orderReturnService.getOrderReturnsAll(pageable);
	}

}
