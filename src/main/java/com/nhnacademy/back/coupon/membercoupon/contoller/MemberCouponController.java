package com.nhnacademy.back.coupon.membercoupon.contoller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.batch.service.BatchService;
import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.request.RequestAllMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberCouponController {

	private final MemberCouponService memberCouponService;
	private final BatchService batchService;


	/**
	 * 관리자가 회원에게 쿠폰을 발급하는 배치 Job을 실행하는 엔드포인트
	 * MQ 에 회원 ID, 쿠폰 ID 를 넣음
	 */
	@Admin
	@PostMapping("/api/admin/memberCoupons/issue")
	public ResponseEntity<Void> issueCouponsToAllMembers(@RequestBody RequestAllMemberCouponDTO request) {
		batchService.issueCouponToActiveMembers(request.getCouponId(), request.getMemberCouponPeriod());
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * 쿠폰함 : 회원 ID로 회원쿠폰 테이블에서 쿠폰 조회
	 */
	@Member
	@GetMapping("/api/auth/mypage/{memberId}/coupons")
	public ResponseEntity<Page<ResponseMemberCouponDTO>> getMemberCouponsByMemberId(@PathVariable String memberId, Pageable pageable) {
		Page<ResponseMemberCouponDTO> response = memberCouponService.getMemberCouponsByMemberId(memberId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * !기능확인용 API -> orderController 의 서비스 구현으로 옭기면 삭제 예정
	 * 사용자 쿠폰 사용 시 사용여부 업데이트 (미사용 -> 사용완료)
	 */
	@PutMapping("/api/memberCoupons")
	public ResponseEntity<Void> updateMemberCouponById(@RequestParam Long memberCouponId) {
		memberCouponService.updateMemberCouponById(memberCouponId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * !기능확인용 API -> orderController 의 서비스 구현으로 옮기면 삭제 예정
	 * 주문취소 시 단일 회원에게 같은 내용의 쿠폰 재발급
	 */
	@PostMapping("/api/admin/memberCoupons/reIssue")
	public ResponseEntity<Void> reIssueCoupon(@RequestParam Long memberCouponId) {
		memberCouponService.reIssueCouponById(memberCouponId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
