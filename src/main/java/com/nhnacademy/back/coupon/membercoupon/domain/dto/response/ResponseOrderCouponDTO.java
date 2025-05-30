package com.nhnacademy.back.coupon.membercoupon.domain.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문서 페이지에서 적용 가능한 쿠폰 정보를 담는 DTO
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseOrderCouponDTO {
	private Long memberCouponId;
	private String couponName;
	private Long couponPolicyMinimum;
	private Long couponPolicyMaximumAmount;
	private Long couponPolicySalePrice;
	private Integer couponPolicyDiscountRate;
	private String couponPolicyName;
	private LocalDateTime memberCouponCreatedAt;
	private LocalDateTime memberCouponPeriod;
	private Long categoryId;
	private String categoryName;
	private Long productId;
	private String productTitle;

}
