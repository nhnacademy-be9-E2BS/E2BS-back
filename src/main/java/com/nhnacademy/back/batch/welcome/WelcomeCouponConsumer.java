package com.nhnacademy.back.batch.welcome;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.exception.CouponNotFoundException;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WelcomeCouponConsumer {

	private final MemberJpaRepository memberRepository;
	private final CouponJpaRepository couponRepository;
	private final MemberCouponJpaRepository memberCouponRepository;

	@RabbitListener(queues = WelcomeCouponRabbitConfig.WELCOME_QUEUE)
	public void issueCoupon(Long customerId) {
		Member member = memberRepository.findById(customerId)
			.orElseThrow(CustomerNotFoundException::new);

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
