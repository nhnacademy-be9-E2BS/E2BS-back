package com.nhnacademy.back.order.order.controller;

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
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderReturnDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderReturnDTO;
import com.nhnacademy.back.order.order.service.OrderService;
import com.nhnacademy.back.order.orderreturn.service.OrderReturnService;
import com.nhnacademy.back.order.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/orders")
public class OrderMemberController {

	private final OrderService orderService;
	private final PaymentService paymentService;
	private final OrderReturnService orderReturnService;

	/**
	 * 포인트 주문에 대한 처리
	 */
	@Member
	@PostMapping("/create/point")
	public ResponseEntity<ResponseOrderResultDTO> createPointOrder(
		@Validated @RequestBody RequestOrderWrapperDTO request,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		return orderService.createPointOrder(request);
	}

	/**
	 * 회원의 주문 취소 처리
	 */
	@Member
	@DeleteMapping("/{orderCode}")
	public ResponseEntity<Void> cancelOrder(@PathVariable String orderCode) {
		return orderService.cancelOrder(orderCode);
	}

	@PostMapping("/return")
	public ResponseEntity<Void> returnOrder(@RequestBody RequestOrderReturnDTO request) {
		return orderService.returnOrder(request);
	}

	@GetMapping("/return")
	public ResponseEntity<Page<ResponseOrderReturnDTO>> getReturnOrders(Pageable pageable,
		@RequestParam String memberId) {
		return orderReturnService.getOrderReturnsByMemberId(memberId, pageable);
	}

	@GetMapping("/return/{orderCode}")
	public ResponseEntity<ResponseOrderReturnDTO> getReturnOrderByOrderCode(@PathVariable String orderCode) {
		return orderReturnService.getOrderReturnByOrderCode(orderCode);
	}

	/**
	 * 회원의 주문 목록 조회
	 */
	@GetMapping
	public ResponseEntity<Page<ResponseOrderDTO>> getOrders(Pageable pageable, @RequestParam String memberId) {
		return ResponseEntity.ok(orderService.getOrdersByMemberId(pageable, memberId));
	}
}
