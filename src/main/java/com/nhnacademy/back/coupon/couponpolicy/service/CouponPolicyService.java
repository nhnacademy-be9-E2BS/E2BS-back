package com.nhnacademy.back.coupon.couponpolicy.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.coupon.couponpolicy.domain.dto.RequestCouponPolicyDTO;
import com.nhnacademy.back.coupon.couponpolicy.domain.dto.ResponseCouponPolicyDTO;

public interface CouponPolicyService {

	/**
	 * 관리자 쿠폰 정책 등록
	 */
	void createCouponPolicy(RequestCouponPolicyDTO couponPolicy);

	/**
	 * 관리자 쿠폰 정책 전체 조회
	 */
	Page<ResponseCouponPolicyDTO> getCouponPolicies(Pageable pageable);

	/**
	 * 관리자 쿠폰 정책 ID 단건 조회
	 */
	ResponseCouponPolicyDTO getCouponPolicyById(Long id);
}
