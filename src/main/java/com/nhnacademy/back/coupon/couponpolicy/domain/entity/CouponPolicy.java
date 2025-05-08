package com.nhnacademy.back.coupon.couponpolicy.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponPolicy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long couponPolicyId;

	@Column(nullable = false, columnDefinition = "bigint DEFAULT 0")
	private long couponPolicyMinimum = 0;

	private Long couponPolicyMaximumAmount;

	private Long couponPolicySalePrice;

	private Integer couponPolicyDiscountRate;

	@Column(nullable = false)
	private LocalDateTime couponPolicyCreatedAt;

	@Column(length = 50, nullable = false)
	private String couponPolicyName;

	public CouponPolicy(long couponPolicyMinimum,
						Long couponPolicyMaximumAmount,
						Long couponPolicySalePrice,
						Integer couponPolicyDiscountRate,
						LocalDateTime couponPolicyCreatedAt,
						String couponPolicyName) {
		this.couponPolicyMinimum = couponPolicyMinimum;
		this.couponPolicyMaximumAmount = couponPolicyMaximumAmount;
		this.couponPolicySalePrice = couponPolicySalePrice;
		this.couponPolicyDiscountRate = couponPolicyDiscountRate;
		this.couponPolicyCreatedAt = couponPolicyCreatedAt;
		this.couponPolicyName = couponPolicyName;
	}
}
