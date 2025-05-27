package com.nhnacademy.back.coupon.membercoupon.domain.dto.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문서 페이지에서 적용 가능한 쿠폰 정보를 담는 DTO
 */
@Setter
@Getter
@NoArgsConstructor
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
	private String CategoryName;
	private String productTitle;

	@QueryProjection
	public ResponseOrderCouponDTO(Long memberCouponId, String couponName, Long couponPolicyMinimum,
		Long couponPolicyMaximumAmount, Long couponPolicySalePrice, Integer couponPolicyDiscountRate,
		String couponPolicyName, LocalDateTime memberCouponCreatedAt, LocalDateTime memberCouponPeriod,
		String categoryName,
		String productTitle) {
		this.memberCouponId = memberCouponId;
		this.couponName = couponName;
		this.couponPolicyMinimum = couponPolicyMinimum;
		this.couponPolicyMaximumAmount = couponPolicyMaximumAmount;
		this.couponPolicySalePrice = couponPolicySalePrice;
		this.couponPolicyDiscountRate = couponPolicyDiscountRate;
		this.couponPolicyName = couponPolicyName;
		this.memberCouponCreatedAt = memberCouponCreatedAt;
		this.memberCouponPeriod = memberCouponPeriod;
		CategoryName = categoryName;
		this.productTitle = productTitle;
	}
}
