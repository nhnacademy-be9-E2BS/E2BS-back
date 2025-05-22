package com.nhnacademy.back.coupon.membercoupon.contoller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMypageMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/{memberId}/coupons")
public class MemberCouponMypageController {

	private final MemberCouponService memberCouponService;

	@GetMapping
	public ResponseEntity<ResponseMypageMemberCouponDTO> getCouponCnt(@PathVariable("memberId") String memberId) {

		ResponseMypageMemberCouponDTO responseMemberCouponDTO = memberCouponService.getMemberCouponCnt(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberCouponDTO);
	}

}
