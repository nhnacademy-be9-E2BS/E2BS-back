package com.nhnacademy.back.coupon.membercoupon.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMypageMemberCouponDTO {

	private String memberId;
	private int couponCnt;

}