package com.nhnacademy.back.coupon.coupon.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
	/**
	 * 활성화 여부로 페이징 조회
	 */
	Page<Coupon> findAllByCouponIsActiveTrue(Pageable pageable);

	/**
	 * 쿠폰 이름 + 활성 상태 true 인 쿠폰 중에서 가장 최근 생성된 쿠폰 조회
	 */
	Optional<Coupon> findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc(String couponName, boolean couponIsActive);

}
