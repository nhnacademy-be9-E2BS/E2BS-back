package com.nhnacademy.back.coupon.couponpolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.coupon.couponpolicy.domain.entity.CouponPolicy;

public interface CouponPolicyJpaRepository extends JpaRepository<CouponPolicy, Long> {
}
