package com.nhnacademy.back.coupon.couponpolicy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.coupon.couponpolicy.domain.dto.RequestCouponPolicyDTO;
import com.nhnacademy.back.coupon.couponpolicy.domain.dto.ResponseCouponPolicyDTO;
import com.nhnacademy.back.coupon.couponpolicy.service.CouponPolicyService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/couponPolicies")
public class CouponPolicyController {

	private final CouponPolicyService couponPolicyService;

	@PostMapping
	public ResponseEntity<Void> createCouponPolicy(@RequestBody RequestCouponPolicyDTO requestCouponPolicyDTO) {
		couponPolicyService.createCouponPolicy(requestCouponPolicyDTO);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<List<ResponseCouponPolicyDTO>> getCouponPolicies() {
		List<ResponseCouponPolicyDTO> couponPolicyDTOs = couponPolicyService.getCouponPolicies();
		return ResponseEntity.status(HttpStatus.OK).body(couponPolicyDTOs);
	}

	@GetMapping("/{couponPolicyId}")
	public ResponseEntity<ResponseCouponPolicyDTO> getCouponPolicyById(@PathVariable Long couponPolicyId) {
		ResponseCouponPolicyDTO responseCouponPolicyDTO = couponPolicyService.getCouponPolicyById(couponPolicyId);
		return ResponseEntity.status(HttpStatus.OK).body(responseCouponPolicyDTO);
	}

}