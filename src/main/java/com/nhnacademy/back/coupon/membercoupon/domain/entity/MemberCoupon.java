package com.nhnacademy.back.coupon.membercoupon.domain.entity;

import java.time.LocalDateTime;

import com.nhnacademy.back.account.member.domain.entity.Member;
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
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCoupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long memberCouponId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(optional = false)
	@JoinColumn(name = "coupon_id")
	private Coupon coupon;

	private LocalDateTime memberCouponCreatedAt;

	private LocalDateTime memberCouponPeriod;

	@Setter
	@Column(nullable = false)
	private boolean memberCouponUsed = false;

	public MemberCoupon(Member member, Coupon coupon, LocalDateTime memberCouponCreatedAt,
		LocalDateTime memberCouponPeriod) {
		this.member = member;
		this.coupon = coupon;
		this.memberCouponCreatedAt = memberCouponCreatedAt;
		this.memberCouponPeriod = memberCouponPeriod;
	}

}
