package com.nhnacademy.back.order.order.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderReturnDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderReturnDTO;
import com.nhnacademy.back.order.order.service.OrderService;
import com.nhnacademy.back.order.orderreturn.service.OrderReturnService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원 주문 API", description = "회원의 주문 관련 기능 제공")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/orders")
public class OrderMemberController {

	private final OrderService orderService;
	private final OrderReturnService orderReturnService;

	/**
	 * 포인트 주문에 대한 처리
	 */
	@Operation(summary = "포인트 결제", description = "회원의 포인트 결제 요청을 처리하는 기능")
	@Member
	@PostMapping("/create/point")
	public ResponseEntity<ResponseOrderResultDTO> createPointOrder(
		@Parameter(description = "주문 상품 정보") @Validated @RequestBody RequestOrderWrapperDTO request,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		return orderService.createPointOrder(request);
	}

	/**
	 * 회원의 주문 취소 처리
	 */
	@Operation(summary = "주문 취소 요청", description = "회원이 배송 대기 상태의 주문에 대해 주문을 취소하는 기능")
	@Member
	@DeleteMapping("/{order-code}")
	public ResponseEntity<Void> cancelOrder(@Parameter(description = "주문 코드") @PathVariable(name = "order-code") String orderCode) {
		return orderService.cancelOrder(orderCode);
	}

	@Operation(summary = "반품 요청", description = "회원의 배송 완료인 주문에 대한 반품 요청을 처리하는 기능")
	@Member
	@PostMapping("/return")
	public ResponseEntity<Void> returnOrder(
		@Parameter(description = "반품 주문 정보") @Validated @RequestBody RequestOrderReturnDTO request,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		return orderService.returnOrder(request);
	}

	@Operation(summary = "반품 목록 조회", description = "특정 회원의 반품 목록을 조회하여 반환하는 기능")
	@Member
	@GetMapping("/return")
	public ResponseEntity<Page<ResponseOrderReturnDTO>> getReturnOrders(Pageable pageable,
		@Parameter(description = "반품 주문을 확인할 회원 ID") @RequestParam String memberId) {
		return orderReturnService.getOrderReturnsByMemberId(memberId, pageable);
	}

	@Operation(summary = "반품 내역 상세 조회", description = "회원의 특정 주문의 반품 상세 내역을 반환하는 기능")
	@Member
	@GetMapping("/return/{order-code}")
	public ResponseEntity<ResponseOrderReturnDTO> getReturnOrderByOrderCode(
		@Parameter(description = "주문 코드") @PathVariable(name = "order-code") String orderCode) {
		return orderReturnService.getOrderReturnByOrderCode(orderCode);
	}

	/**
	 * 회원의 주문 목록 조회
	 */
	@Operation(summary = "주문 목록 조회", description = "회원이 자신 주문 내역 목록을 필터링 조건에 따라 조회할 수 있는 기능")
	@Member
	@GetMapping
	public ResponseEntity<Page<ResponseOrderDTO>> getOrders(Pageable pageable,
		@Parameter(description = "회원 ID") @RequestParam String memberId,
		@Parameter(description = "주문 상태") @RequestParam(required = false) String stateName,
		@Parameter(description = "주문 일자 시작 일") @RequestParam(required = false) String startDate,
		@Parameter(description = "주문 일자 끝 일") @RequestParam(required = false) String endDate,
		@Parameter(description = "주문 코드") @RequestParam(required = false) String orderCode) {
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
			orderService.getOrdersByMemberId(pageable, memberId, stateName, start, end, orderCode));
	}
}
