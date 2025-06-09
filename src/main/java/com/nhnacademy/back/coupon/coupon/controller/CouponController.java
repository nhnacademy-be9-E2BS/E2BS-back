package com.nhnacademy.back.coupon.coupon.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.coupon.coupon.domain.dto.request.RequestCouponDTO;
import com.nhnacademy.back.coupon.coupon.domain.dto.response.ResponseCouponDTO;
import com.nhnacademy.back.coupon.coupon.service.CouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/coupons")
@Tag(name = "쿠폰", description = "관리자 쿠폰 관리 API")
public class CouponController {

	private final CouponService couponService;

	@Operation(summary = "쿠폰 생성", description = "입력받은 정보로 쿠폰을 생성",
		responses = {
			@ApiResponse(responseCode = "201", description = "쿠폰 생성 성공"),
			@ApiResponse(responseCode = "400", description = "입력값 유효성 검증 실패",
				content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PostMapping
	public ResponseEntity<Void> createCoupon(
		@Validated @RequestBody RequestCouponDTO request,
		BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		couponService.createCoupon(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "쿠폰 전체 조회", description = "등록된 모든 쿠폰을 페이지네이션하여 조회")
	@Admin
	@GetMapping
	public ResponseEntity<Page<ResponseCouponDTO>> getCoupons(
		@Parameter(hidden = true) Pageable pageable) {
		Page<ResponseCouponDTO> coupons = couponService.getCoupons(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(coupons);
	}

	@Operation(summary = "쿠폰 단건 조회", description = "쿠폰 ID로 단일 쿠폰 정보 조회",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공"),
			@ApiResponse(responseCode = "404", description = "해당 쿠폰 없음")
		})
	@Admin
	@GetMapping("/{couponId}")
	public ResponseEntity<ResponseCouponDTO> getCoupon(
		@Parameter(description = "쿠폰 ID", example = "1")
		@PathVariable Long couponId) {
		ResponseCouponDTO response = couponService.getCoupon(couponId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "쿠폰 활성 여부 변경", description = "쿠폰의 활성 상태 토글")
	@Admin
	@PutMapping("/{couponId}")
	public ResponseEntity<Void> updateCoupon(
		@Parameter(description = "쿠폰 ID", example = "1")
		@PathVariable Long couponId) {
		couponService.updateCouponIsActive(couponId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "활성화된 쿠폰 조회", description = "현재 활성 상태인 쿠폰 목록 조회")
	@Admin
	@GetMapping("/isActive")
	public ResponseEntity<Page<ResponseCouponDTO>> getCouponsIsActive(
		@Parameter(hidden = true) Pageable pageable) {
		Page<ResponseCouponDTO> coupons = couponService.getCouponsIsActive(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(coupons);
	}
}
