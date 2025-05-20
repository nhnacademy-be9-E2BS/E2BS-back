package com.nhnacademy.back.coupon.membercoupon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;

public interface MemberCouponJpaRepository extends JpaRepository<MemberCoupon, Long> {

	/**
	 * 쿠폰함 : 회원 ID로 쿠폰 조회
	 */
	Page<MemberCoupon> findByMember_CustomerId(Long memberId, Pageable pageable);
}
