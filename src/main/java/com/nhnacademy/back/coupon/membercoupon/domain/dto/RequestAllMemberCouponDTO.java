package com.nhnacademy.back.coupon.membercoupon.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAllMemberCouponDTO {
	private Long couponId;
	private LocalDateTime memberCouponPeriod;
}
