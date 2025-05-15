package com.nhnacademy.back.coupon.membercoupon.service;

import com.nhnacademy.back.coupon.membercoupon.domain.dto.RequestAllMemberCouponDTO;

public interface MemberCouponService {

	/**
	 * 관리자가 전체 회원에게 쿠폰을 발급
	 */
	void issueCouponToAllMembers(RequestAllMemberCouponDTO request);
}
