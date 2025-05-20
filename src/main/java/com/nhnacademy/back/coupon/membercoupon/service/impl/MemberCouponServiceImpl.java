package com.nhnacademy.back.coupon.membercoupon.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberCouponServiceImpl implements MemberCouponService {

	private final MemberCouponJpaRepository memberCouponJpaRepository;
	private final MemberJpaRepository memberJpaRepository;

	@Transactional
	public ResponseMemberCouponDTO getMemberCouponCnt(String memberId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException("아이디에 해당하는 회원을 찾지 못했습니다.");
		}

		List<MemberCoupon> memberCoupons = memberCouponJpaRepository.getMemberCouponsByCustomer(member.getCustomer());
		int couponCnt = 0;
		if (Objects.nonNull(memberCoupons) && !memberCoupons.isEmpty()) {
			couponCnt = memberCoupons.size();
		}

		return new ResponseMemberCouponDTO(memberId, couponCnt);
	}

}
