package com.nhnacademy.back.order.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.order.order.model.dto.response.ResponseMemberOrderDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseMemberRecentOrderDTO;
import com.nhnacademy.back.order.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/mypage/{memberId}/orders")
public class OrderMypageController {

	private final OrderService orderService;

	/**
	 * 마이페이지 회원 주문 총 건 수 조회
	 */
	@GetMapping("/counts")
	public ResponseEntity<ResponseMemberOrderDTO> getMemberOrders(@PathVariable("memberId") String memberId) {
		ResponseMemberOrderDTO responseMemberOrderDTO = orderService.getMemberOrdersCnt(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberOrderDTO);
	}

	/**
	 * 최근 주문한 제품 조회
	 */
	@GetMapping
	public ResponseEntity<List<ResponseMemberRecentOrderDTO>> getMemberRecentOrders(
		@PathVariable("memberId") String memberId) {
		List<ResponseMemberRecentOrderDTO> responseMemberRecentOrderDTOS = orderService.getMemberRecentOrders(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberRecentOrderDTOS);
	}

}
