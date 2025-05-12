package com.nhnacademy.back.coupon.coupon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.coupon.coupon.domain.dto.request.RequestCouponDTO;
import com.nhnacademy.back.coupon.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/coupons")
public class CouponController {

	private final CouponService couponService;

	/**
	 * 관리자 쿠폰 생성
	 */
	@PostMapping
	public ResponseEntity<Void> createCoupon(@RequestBody RequestCouponDTO request) {

		return null;
	}

	/**
	 * 관리자 쿠폰 조회
	 */
	// @GetMapping
	// public ResponseEntity<>

	/**
	 * 관리자 쿠폰 삭제
	 */

}
