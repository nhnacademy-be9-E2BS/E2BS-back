package com.nhnacademy.back.batch.welcome;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.common.config.RabbitConfig;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.exception.CouponNotFoundException;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WelcomeCouponConsumer {

	private final MemberJpaRepository memberRepository;
	private final CouponJpaRepository couponRepository;
	private final MemberCouponJpaRepository memberCouponRepository;

	@RabbitListener(queues = RabbitConfig.WELCOME_QUEUE)
	public void issueCoupon(Long customerId) {
		try {
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

			log.info("웰컴 쿠폰 발급 성공 - memberId: {}", customerId);

		} catch (CustomerNotFoundException | CouponNotFoundException e) {
			log.error("웰컴 쿠폰 발급 실패 - {}", e.getMessage());
			throw e; // DLQ 로 넘기기 위한 예외 재전파 (throw 해야 DLQ 로 전파됨)
		} catch (Exception e) {
			log.error("웰컴 쿠폰 발급 실패", e);
			throw e;
		}
	}
}
