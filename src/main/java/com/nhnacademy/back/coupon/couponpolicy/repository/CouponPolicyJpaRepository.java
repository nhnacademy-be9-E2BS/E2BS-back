package com.nhnacademy.back.coupon.couponpolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.coupon.couponpolicy.domain.entity.CouponPolicy;

public interface CouponPolicyJpaRepository extends JpaRepository<CouponPolicy, Long> {

	/**
	 * 지정된 쿠폰 정책 이름에 해당하는 쿠폰의 유무 확인
	 *
	 * @param couponPolicyName 쿠폰 정책의 이름
	 * @return 해당 쿠폰 정책 이름에 매칭되는 쿠폰의 유무
	 */
	boolean existsByCouponPolicyName(String couponPolicyName);
}
