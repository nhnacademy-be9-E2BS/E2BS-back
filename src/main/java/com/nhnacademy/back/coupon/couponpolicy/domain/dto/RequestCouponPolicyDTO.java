package com.nhnacademy.back.coupon.couponpolicy.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestCouponPolicyDTO {
	private long couponPolicyMinimum;
	private Long couponPolicyMaximumAmount;
	private Long couponPolicySalePrice;
	private Integer couponPolicyDiscountRate;
	private LocalDateTime couponPolicyCreatedAt;
	private String couponPolicyName;
}
