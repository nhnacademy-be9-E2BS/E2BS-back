package com.nhnacademy.back.coupon.coupon.service.impl;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.coupon.coupon.domain.dto.request.RequestCouponDTO;
import com.nhnacademy.back.coupon.coupon.domain.dto.response.ResponseCouponDTO;
import com.nhnacademy.back.coupon.coupon.repository.CategoryCouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.repository.ProductCouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

	private CouponJpaRepository couponJpaRepository;
	private CategoryCouponJpaRepository categoryCouponJpaRepository;
	private ProductCouponJpaRepository productCouponJpaRepository;

	/**
	 * 관리자가 쿠폰을 생성
	 * 카테고리 쿠폰일 경우 categoryCoupon 테이블에도 추가
	 * 상품 쿠폰일 경우 productCoupon 테이블에도 추가
	 */
	@Override
	public void createCoupon(RequestCouponDTO request) {
		if(Objects.isNull(request)) {

		}
	}

	@Override
	public Page<ResponseCouponDTO> getCoupons(Pageable pageable) {
		return null;
	}

	@Override
	public ResponseCouponDTO getCoupon(Long couponId) {
		return null;
	}
}
