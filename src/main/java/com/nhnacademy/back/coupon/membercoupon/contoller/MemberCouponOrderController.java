package com.nhnacademy.back.coupon.membercoupon.contoller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.cart.domain.dto.request.RequestCartOrderDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseOrderCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberCouponOrderController {

	private final MemberCouponService memberCouponService;

	/**
	 * 주문서에서 적용가능한 쿠폰 리스트 조회
	 */
	@GetMapping("/api/order/{memberId}/coupons")
	public ResponseEntity<List<ResponseOrderCouponDTO>> getCouponsInOrder(@PathVariable("memberId") String memberId, @RequestBody RequestCartOrderDTO request) {
		List<ResponseOrderCouponDTO> response = memberCouponService.getCouponsInOrderByMemberIdAndProductIds(memberId, request.getProductIds());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
