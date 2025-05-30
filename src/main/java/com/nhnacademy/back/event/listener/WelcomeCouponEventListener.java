package com.nhnacademy.back.event.listener;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.exception.CouponNotFoundException;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;
import com.nhnacademy.back.event.event.RegisterPointEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WelcomeCouponEventListener {

	private final MemberJpaRepository memberRepository;
	private final CouponJpaRepository couponRepository;
	private final MemberCouponJpaRepository memberCouponRepository;

	@Async
	@EventListener
	public void handleWelcomeCouponEvent(RegisterPointEvent event) {
		String memberId = event.getMemberId();

		Member member = memberRepository.getMemberByMemberId(memberId);

		String welcomeCouponName = "웰컴 쿠폰";

		Coupon welcomeCoupon = couponRepository
			.findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc(welcomeCouponName, true)
			.orElseThrow(() -> new CouponNotFoundException("활성화된 웰컴 쿠폰이 존재하지 않습니다."));

		MemberCoupon memberCoupon = new MemberCoupon(
			member,
			welcomeCoupon,
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(30)
		);
		memberCouponRepository.save(memberCoupon);

	}
}
