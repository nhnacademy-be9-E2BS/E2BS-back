package com.nhnacademy.back.batch.admin;

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
public class AdminCouponConsumer {

	private final MemberJpaRepository memberRepository;
	private final CouponJpaRepository couponRepository;
	private final MemberCouponJpaRepository memberCouponRepository;

	@RabbitListener(queues = RabbitConfig.DIRECT_QUEUE)
	public void issueCoupon(AdminIssueMessage message) {
		Member member = memberRepository.findById(message.getMemberId())
			.orElseThrow(CustomerNotFoundException::new);

		Coupon coupon = couponRepository.findById(message.getCouponId())
			.orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

		MemberCoupon memberCoupon = new MemberCoupon(
			member,
			coupon,
			LocalDateTime.now(),
			message.getExpireAt()
		);
		memberCouponRepository.save(memberCoupon);
		log.info("관리자 쿠폰 발급 완료");
	}
}
