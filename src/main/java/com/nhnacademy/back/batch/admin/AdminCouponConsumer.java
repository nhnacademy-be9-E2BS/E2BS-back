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
		try {
			/*
			 * batch 에서 검증 후 메시지를 MQ 로 보내지만,
			 * 리스너 입장에서 외부 메시지에 대해 신뢰할 수 없으므로 다시 재검증
			 * 발급도중 member 나 coupon 의 삭제 가능성
			 */
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

			log.info("관리자 쿠폰 발급 성공 - memberId: {}, couponId: {}", message.getMemberId(), message.getCouponId());

		} catch (CustomerNotFoundException | CouponNotFoundException e) {
			log.error("관리자 쿠폰 발급 실패 - {}", e.getMessage());
			throw e; // DLQ 로 넘기기 위한 예외 재전파 (throw 해야 DLQ 로 전파됨)
		} catch (Exception e) {
			log.error("관리자 쿠폰 발급 실패 - message: {}", message, e);
			throw e;
		}
	}
}
