package com.nhnacademy.back.coupon.coupon.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
	// 활성화 여부로 페이징 조회
	Page<Coupon> findAllByCouponIsActiveTrue(Pageable pageable);
}
