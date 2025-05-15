package com.nhnacademy.back.coupon.membercoupon.domain.entity;

import java.time.LocalDateTime;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;

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

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCoupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long memberCouponId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne(optional = false)
	@JoinColumn(name = "coupon_id")
	private Coupon coupon;

	private LocalDateTime memberCouponCreatedAt;

	private LocalDateTime memberCouponPeriod;

	@Column(nullable = false)
	private boolean memberCouponUsed = false;
}
