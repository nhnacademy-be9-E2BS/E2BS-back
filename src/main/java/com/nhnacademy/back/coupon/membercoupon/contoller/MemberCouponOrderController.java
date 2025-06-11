package com.nhnacademy.back.coupon.membercoupon.contoller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseOrderCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "주문서 쿠폰 조회", description = "회원의 주문서에서 적용 가능한 쿠폰 조회 API")
public class MemberCouponOrderController {

	private final MemberCouponService memberCouponService;

	@Operation(
		summary = "주문서 적용 가능 쿠폰 조회",
		description = "회원 ID와 상품 ID 리스트를 받아 해당 주문서에 적용 가능한 쿠폰을 조회"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주문서 쿠폰 조회 성공"),
		@ApiResponse(responseCode = "400", description = "요청 파라미터 오류")
	})
	@GetMapping("/api/order/{member-id}/coupons")
	public ResponseEntity<List<ResponseOrderCouponDTO>> getCouponsInOrder(
		@Parameter(description = "회원 ID", example = "member123", required = true)
		@PathVariable("member-id") String memberId,

		@Parameter(
			description = "상품 ID 리스트",
			required = true,
			array = @ArraySchema(schema = @Schema(type = "long"))
		)
		@RequestParam List<Long> request) {

		List<ResponseOrderCouponDTO> response =
			memberCouponService.getCouponsInOrderByMemberIdAndProductIds(memberId, request);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
