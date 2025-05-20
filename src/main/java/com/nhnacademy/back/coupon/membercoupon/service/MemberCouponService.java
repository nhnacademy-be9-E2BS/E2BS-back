package com.nhnacademy.back.coupon.membercoupon.service;

import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;

public interface MemberCouponService {

	ResponseMemberCouponDTO getMemberCouponCnt(String memberId);

}
