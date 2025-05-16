package com.nhnacademy.back.coupon.couponpolicy.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.coupon.couponpolicy.domain.dto.RequestCouponPolicyDTO;
import com.nhnacademy.back.coupon.couponpolicy.domain.dto.ResponseCouponPolicyDTO;
import com.nhnacademy.back.coupon.couponpolicy.service.CouponPolicyService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/couponPolicies")
public class CouponPolicyController {

	private final CouponPolicyService couponPolicyService;

	/**
	 * 관리자 쿠폰 정책 등록
	 */
	@Admin
	@PostMapping
	public ResponseEntity<Void> createCouponPolicy(@RequestBody RequestCouponPolicyDTO requestCouponPolicyDTO) {
		couponPolicyService.createCouponPolicy(requestCouponPolicyDTO);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 관리자 쿠폰 정책 전체 조회
	 */
	@Admin
	@GetMapping
	public ResponseEntity<Page<ResponseCouponPolicyDTO>> getCouponPolicies(Pageable pageable) {
		Page<ResponseCouponPolicyDTO> couponPolicyDTOs = couponPolicyService.getCouponPolicies(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(couponPolicyDTOs);
	}

	/**
	 * 관리자 쿠폰 정책 ID로 단건 조회
	 */
	@Admin
	@GetMapping("/{couponPolicyId}")
	public ResponseEntity<ResponseCouponPolicyDTO> getCouponPolicyById(@PathVariable Long couponPolicyId) {
		ResponseCouponPolicyDTO responseCouponPolicyDTO = couponPolicyService.getCouponPolicyById(couponPolicyId);
		return ResponseEntity.status(HttpStatus.OK).body(responseCouponPolicyDTO);
	}

}