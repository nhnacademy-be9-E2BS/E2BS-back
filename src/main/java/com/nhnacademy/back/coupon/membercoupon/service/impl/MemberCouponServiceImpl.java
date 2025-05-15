package com.nhnacademy.back.coupon.membercoupon.service.impl;

import org.springframework.stereotype.Service;

import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.RequestAllMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberCouponServiceImpl implements MemberCouponService {

	private final MemberCouponJpaRepository memberCouponJpaRepository;
	private final MemberJpaRepository memberJpaRepository;

	/**
	 * 관리자가 전체 회원에게 쿠폰을 발급
	 */
	@Override
	public void issueCouponToAllMembers(RequestAllMemberCouponDTO request) {
		
	}
}
