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

	@Column(nullable = false, columnDefinition = "integer DEFAULT 0")
	private int couponPolicyMinimum = 0;

	@Column()
	private int couponPolicyMaximumAmount;

	@Column()
	private int couponPolicySalePrice;

	@Column()
	private int couponPolicyDiscountRate;

	@Column(nullable = false)
	private LocalDateTime couponPolicyCreatedAt;

	@Column(length = 50, nullable = false)
	private String couponPolicyName;
}
