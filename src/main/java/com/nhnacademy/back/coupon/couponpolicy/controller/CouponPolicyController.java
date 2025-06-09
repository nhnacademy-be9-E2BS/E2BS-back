package com.nhnacademy.back.coupon.couponpolicy.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.coupon.couponpolicy.domain.dto.*;
import com.nhnacademy.back.coupon.couponpolicy.service.CouponPolicyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "쿠폰 정책", description = "쿠폰 정책 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/couponPolicies")
public class CouponPolicyController {

	private final CouponPolicyService couponPolicyService;

	@Operation(
		summary = "쿠폰 정책 생성",
		description = "관리자가 쿠폰 정책 생성",
		responses = {
			@ApiResponse(responseCode = "201", description = "쿠폰 정책 생성 성공"),
			@ApiResponse(responseCode = "400", description = "요청값 검증 실패"),
			@ApiResponse(responseCode = "401", description = "인증 실패")
		}
	)
	@Admin
	@PostMapping
	public ResponseEntity<Void> createCouponPolicy(@RequestBody RequestCouponPolicyDTO requestCouponPolicyDTO) {
		couponPolicyService.createCouponPolicy(requestCouponPolicyDTO);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(
		summary = "쿠폰 정책 전체 조회",
		description = "등록된 모든 쿠폰 정책 목록을 조회",
		parameters = {
			@Parameter(name = "page", description = "페이지 번호", example = "0"),
			@Parameter(name = "size", description = "페이지 크기", example = "10")
		},
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseCouponPolicyDTO.class))))
		}
	)
	@Admin
	@GetMapping
	public ResponseEntity<Page<ResponseCouponPolicyDTO>> getCouponPolicies(Pageable pageable) {
		Page<ResponseCouponPolicyDTO> couponPolicyDTOs = couponPolicyService.getCouponPolicies(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(couponPolicyDTOs);
	}

	@Operation(
		summary = "쿠폰 정책 단건 조회",
		description = "쿠폰 정책 ID로 단일 쿠폰 정책을 조회",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공",
				content = @Content(schema = @Schema(implementation = ResponseCouponPolicyDTO.class))),
			@ApiResponse(responseCode = "404", description = "해당 ID의 쿠폰 정책이 존재하지 않음")
		}
	)
	@Admin
	@GetMapping("/{couponPolicyId}")
	public ResponseEntity<ResponseCouponPolicyDTO> getCouponPolicyById(
		@Parameter(description = "쿠폰 정책 ID", example = "1") @PathVariable Long couponPolicyId) {
		ResponseCouponPolicyDTO responseCouponPolicyDTO = couponPolicyService.getCouponPolicyById(couponPolicyId);
		return ResponseEntity.status(HttpStatus.OK).body(responseCouponPolicyDTO);
	}
}
