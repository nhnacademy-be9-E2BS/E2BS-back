package com.nhnacademy.back.coupon.membercoupon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMypageMemberCouponDTO;

public interface MemberCouponService {

	ResponseMypageMemberCouponDTO getMemberCouponCnt(String memberId);

	/**
	 * 회원 ID로 쿠폰 조회
	 */
	Page<ResponseMemberCouponDTO> getMemberCouponsByMemberId(String memberId, Pageable pageable);

	/**
	 * 회원이 쿠폰 사용 시 사용여부 업데이트 (미사용 -> 사용완료)
	 */
	void updateMemberCouponById(Long memberCouponId);

	/**
	 * 주문 취소 시 회원이 사용한 쿠폰과 동일한 내용의 쿠폰을 재발급
	 */
	void reIssueCouponById(Long memberCouponId);
}
