package com.nhnacademy.back.coupon.couponpolicy.service;

import java.util.List;

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
	List<ResponseCouponPolicyDTO> getCouponPolicies();

	/**
	 * 관리자 쿠폰 정책 ID 단건 조회
	 */
	ResponseCouponPolicyDTO getCouponPolicyById(Long id);
}
