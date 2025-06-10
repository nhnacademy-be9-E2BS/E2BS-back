package com.nhnacademy.back.coupon.membercoupon.contoller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.batch.service.AdminIssueBatchService;
import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.request.RequestAllMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원 쿠폰", description = "회원 쿠폰 발급 및 조회 기능 제공")
@RestController
@RequiredArgsConstructor
public class MemberCouponController {

	private final MemberCouponService memberCouponService;
	private final AdminIssueBatchService adminIssueBatchService;

	/**
	 * 관리자가 회원에게 쿠폰을 발급하는 배치 Job을 실행하는 엔드포인트
	 */
	@Operation(
		summary = "회원 전체에 쿠폰 발급",
		description = "관리자가 배치 Job을 통해 전체 활성 회원에게 쿠폰을 발급",
		responses = {
			@ApiResponse(responseCode = "200", description = "쿠폰 발급 요청 성공"),
			@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content)
		}
	)
	@Admin
	@PostMapping("/api/admin/memberCoupons/issue")
	public ResponseEntity<Void> issueCouponsToAllMembers(
		@RequestBody @Parameter(description = "활성 회원에게 발급할 쿠폰 ID 및 사용 기간", required = true)
		RequestAllMemberCouponDTO request) {
		adminIssueBatchService.issueCouponToActiveMembers(request.getCouponId(), request.getMemberCouponPeriod());
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * 쿠폰함 : 회원 ID로 회원쿠폰 테이블에서 쿠폰 조회
	 */
	@Operation(
		summary = "회원의 전체 쿠폰 조회",
		description = "회원 ID를 기준으로 전체 쿠폰을 조회",
		responses = @ApiResponse(responseCode = "200", description = "조회 성공")
	)
	@Member
	@GetMapping("/api/auth/mypage/{member-id}/coupons")
	public ResponseEntity<Page<ResponseMemberCouponDTO>> getMemberCouponsByMemberId(
		@Parameter(description = "회원 ID", example = "user") @PathVariable("member-id") String memberId,
		@Parameter(hidden = true) Pageable pageable) {

		Page<ResponseMemberCouponDTO> response = memberCouponService.getMemberCouponsByMemberId(memberId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 사용 가능한 쿠폰 조회
	 */
	@Operation(
		summary = "사용 가능한 회원 쿠폰 조회",
		description = "회원 ID를 기준으로 현재 사용 가능한 쿠폰만 조회",
		responses = @ApiResponse(responseCode = "200", description = "조회 성공")
	)
	@Member
	@GetMapping("/api/auth/mypage/{member-id}/couponsUsable")
	public ResponseEntity<Page<ResponseMemberCouponDTO>> getUsableMemberCouponsByMemberId(
		@Parameter(description = "회원 ID", example = "user") @PathVariable("member-id") String memberId,
		@Parameter(hidden = true) Pageable pageable) {

		Page<ResponseMemberCouponDTO> response = memberCouponService.getUsableMemberCouponsByMemberId(memberId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 사용 불가능한 쿠폰 조회
	 */
	@Operation(
		summary = "사용 불가능한 회원 쿠폰 조회",
		description = "회원 ID를 기준으로 현재 사용 불가능한 쿠폰만 조회",
		responses = @ApiResponse(responseCode = "200", description = "조회 성공")
	)
	@Member
	@GetMapping("/api/auth/mypage/{member-id}/couponsUnusable")
	public ResponseEntity<Page<ResponseMemberCouponDTO>> getUnusableMemberCouponsByMemberId(
		@Parameter(description = "회원 ID", example = "user") @PathVariable("member-id") String memberId,
		@Parameter(hidden = true) Pageable pageable) {

		Page<ResponseMemberCouponDTO> response = memberCouponService.getUnusableMemberCouponsByMemberId(memberId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
