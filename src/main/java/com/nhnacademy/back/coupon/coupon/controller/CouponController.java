package com.nhnacademy.back.coupon.coupon.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.coupon.coupon.domain.dto.request.RequestCouponDTO;
import com.nhnacademy.back.coupon.coupon.domain.dto.response.ResponseCouponDTO;
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
	public ResponseEntity<Void> createCoupon(@Validated @RequestBody RequestCouponDTO request, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		couponService.createCoupon(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 관리자 쿠폰 전체 조회
	 */
	@GetMapping
	public ResponseEntity<Page<ResponseCouponDTO>> getCoupons(Pageable pageable) {
		Page<ResponseCouponDTO> coupons = couponService.getCoupons(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(coupons);
	}

	/**
	 * 관리자 쿠폰 ID로 단건 조회
	 */
	@GetMapping("/{couponId}")
	public ResponseEntity<ResponseCouponDTO> getCoupon(@PathVariable Long couponId) {
		ResponseCouponDTO response = couponService.getCoupon(couponId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 관리자 쿠폰 활성 여부 변경
	 */
	@PutMapping("/{couponId}")
	public ResponseEntity<Void> updateCoupon(@PathVariable Long couponId) {
		couponService.updateCouponIsActive(couponId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * 관리자 쿠폰 발급시 활성화된 쿠폰만 조회
	 */
	@GetMapping("/isActive")
	public ResponseEntity<Page<ResponseCouponDTO>> getCouponsIsActive(Pageable pageable) {
		Page<ResponseCouponDTO> coupons = couponService.getCouponsIsActive(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(coupons);
	}

}
