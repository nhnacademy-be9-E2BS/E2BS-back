package com.nhnacademy.back.coupon.couponpolicy.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.coupon.couponpolicy.domain.dto.RequestCouponPolicyDTO;
import com.nhnacademy.back.coupon.couponpolicy.domain.dto.ResponseCouponPolicyDTO;
import com.nhnacademy.back.coupon.couponpolicy.domain.entity.CouponPolicy;
import com.nhnacademy.back.coupon.couponpolicy.exception.CouponPolicyAlreadyExistException;
import com.nhnacademy.back.coupon.couponpolicy.exception.CouponPolicyNotFoundException;
import com.nhnacademy.back.coupon.couponpolicy.repository.CouponPolicyJpaRepository;
import com.nhnacademy.back.coupon.couponpolicy.service.CouponPolicyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponPolicyServiceImpl implements CouponPolicyService {

	private final CouponPolicyJpaRepository couponPolicyJpaRepository;

	@Override
	@Transactional
	public void createCouponPolicy(RequestCouponPolicyDTO requestDTO) {
		if(couponPolicyJpaRepository.existsByCouponPolicyName(requestDTO.getCouponPolicyName())) {
			throw new CouponPolicyAlreadyExistException("Coupon Policy Already Exist: " + requestDTO.getCouponPolicyName());
		}
		CouponPolicy couponPolicy = new CouponPolicy(
			requestDTO.getCouponPolicyMinimum(),
			requestDTO.getCouponPolicyMaximumAmount(),
			requestDTO.getCouponPolicySalePrice(),
			requestDTO.getCouponPolicyDiscountRate(),
			requestDTO.getCouponPolicyCreatedAt(),
			requestDTO.getCouponPolicyName());

		couponPolicyJpaRepository.save(couponPolicy);
	}

	@Override
	public Page<ResponseCouponPolicyDTO> getCouponPolicies(Pageable pageable) {
		return couponPolicyJpaRepository.findAll(pageable)
			.map(couponPolicy -> new ResponseCouponPolicyDTO(
				couponPolicy.getCouponPolicyId(),
				couponPolicy.getCouponPolicyMinimum(),
				couponPolicy.getCouponPolicyMaximumAmount(),
				couponPolicy.getCouponPolicySalePrice(),
				couponPolicy.getCouponPolicyDiscountRate(),
				couponPolicy.getCouponPolicyCreatedAt(),
				couponPolicy.getCouponPolicyName()
			));
	}


	@Override
	public ResponseCouponPolicyDTO getCouponPolicyById(Long id) {
		CouponPolicy couponPolicy = couponPolicyJpaRepository.findById(id)
			.orElseThrow(() -> new CouponPolicyNotFoundException("Coupon Policy Not Found: " + id));

		return new ResponseCouponPolicyDTO(
			couponPolicy.getCouponPolicyId(),
			couponPolicy.getCouponPolicyMinimum(),
			couponPolicy.getCouponPolicyMaximumAmount(),
			couponPolicy.getCouponPolicySalePrice(),
			couponPolicy.getCouponPolicyDiscountRate(),
			couponPolicy.getCouponPolicyCreatedAt(),
			couponPolicy.getCouponPolicyName()
		);
	}

}
