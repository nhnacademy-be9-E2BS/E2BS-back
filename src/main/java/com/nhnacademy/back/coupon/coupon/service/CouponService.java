package com.nhnacademy.back.coupon.coupon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.coupon.coupon.domain.dto.request.RequestCouponDTO;
import com.nhnacademy.back.coupon.coupon.domain.dto.response.ResponseCouponDTO;

public interface CouponService {

	/**
	 * 쿠폰 생성
	 */
	void createCoupon(RequestCouponDTO request);

	/**
	 * 쿠폰 전체 조회
	 */
	Page<ResponseCouponDTO> getCoupons(Pageable pageable);

	/**
	 * 쿠폰 단건 조회
	 */
	ResponseCouponDTO getCoupon(Long couponId);

	/**
	 * 쿠폰 상태 변경 (활성 <-> 비활성)
	 */
	void updateCouponIsActive(Long couponId);
}
