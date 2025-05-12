package com.nhnacademy.back.coupon.coupon.service.impl;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.common.exception.BadRequestException;
import com.nhnacademy.back.coupon.coupon.domain.dto.request.RequestCouponDTO;
import com.nhnacademy.back.coupon.coupon.domain.dto.response.ResponseCouponDTO;
import com.nhnacademy.back.coupon.coupon.domain.entity.CategoryCoupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.repository.CategoryCouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.repository.ProductCouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.service.CouponService;
import com.nhnacademy.back.coupon.couponpolicy.domain.entity.CouponPolicy;
import com.nhnacademy.back.coupon.couponpolicy.exception.CouponPolicyNotFoundException;
import com.nhnacademy.back.coupon.couponpolicy.repository.CouponPolicyJpaRepository;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

	private CouponPolicyJpaRepository couponPolicyJpaRepository;
	private CouponJpaRepository couponJpaRepository;
	private CategoryCouponJpaRepository categoryCouponJpaRepository;
	private ProductCouponJpaRepository productCouponJpaRepository;
	private CategoryJpaRepository categoryJpaRepository;

	/**
	 * 관리자가 쿠폰을 생성
	 * 카테고리 쿠폰일 경우 categoryCoupon 테이블에도 추가
	 * 상품 쿠폰일 경우 productCoupon 테이블에도 추가
	 */
	@Override
	public void createCoupon(RequestCouponDTO request) {
		if(Objects.isNull(request)) {
			throw new BadRequestException("쿠폰 생성 요청 DTO 를 받지 못했습니다.");
		}

		CouponPolicy couponPolicy = couponPolicyJpaRepository.findById(request.getCouponPolicyId())
			.orElseThrow(()-> new CouponPolicyNotFoundException("존재하지 않는 쿠폰 정책입니다"));

		Coupon coupon = new Coupon(couponPolicy, request.getCouponName());

		// Coupon savedCoupon = couponJpaRepository.save(coupon);
		// Long id = savedCoupon.getCouponId();

		if(request.getCategoryId() != null) {
			Category category = categoryJpaRepository.findById(request.getCategoryId())
				.orElseThrow(); // todo CategoryNotFoundException 추가 예정
			CategoryCoupon categoryCoupon = new CategoryCoupon(coupon, category);
			categoryCouponJpaRepository.save(categoryCoupon);
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
