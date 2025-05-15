package com.nhnacademy.back.coupon.coupon.domain.entity;

import com.nhnacademy.back.coupon.couponpolicy.domain.entity.CouponPolicy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long couponId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "coupon_policy_id")
	private CouponPolicy couponPolicy;

	@Column(length = 30, nullable = false)
	private String couponName;

	@Setter
	@Column(nullable = false)
	private boolean couponIsActive = true;

	public Coupon(CouponPolicy couponPolicy, String couponName) {
		this.couponPolicy = couponPolicy;
		this.couponName = couponName;
	}

}
