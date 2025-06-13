package com.nhnacademy.back.coupon.membercoupon.contoller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMypageMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/mypage/{member-id}/coupons/counts")
@Tag(name = "마이페이지 사용가능 쿠폰 개수", description = "마이페이지에서 회원의 쿠폰 개수 조회")
public class MemberCouponMypageController {

	private final MemberCouponService memberCouponService;

	@Operation(
		summary = "사용가능한 쿠폰 개수 조회",
		description = "회원 ID를 기준으로 사용가능한 쿠폰 개수를 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "201", description = "쿠폰 개수 조회 성공"),
			@ApiResponse(responseCode = "404", description = "해당 회원 ID가 존재하지 않음")
		}
	)
	@GetMapping
	public ResponseEntity<ResponseMypageMemberCouponDTO> getCouponCnt(
		@Parameter(description = "회원 ID", example = "member123", required = true)
		@PathVariable("member-id") String memberId) {

		ResponseMypageMemberCouponDTO responseMemberCouponDTO = memberCouponService.getMemberCouponCnt(memberId);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberCouponDTO);
	}
}
