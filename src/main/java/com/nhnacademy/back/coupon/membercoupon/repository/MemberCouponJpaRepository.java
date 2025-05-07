package com.nhnacademy.back.coupon.membercoupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;

public interface MemberCouponJpaRepository extends JpaRepository<MemberCoupon, Long> {
}
