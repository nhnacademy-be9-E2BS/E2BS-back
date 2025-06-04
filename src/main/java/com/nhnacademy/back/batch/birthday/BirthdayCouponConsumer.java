package com.nhnacademy.back.batch.birthday;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

@Component
@RequiredArgsConstructor
public class BirthdayCouponConsumer {

	private final MemberJpaRepository memberRepository;
	private final CouponJpaRepository couponRepository;
	private final MemberCouponJpaRepository memberCouponRepository;

	@RabbitListener(queues = RabbitConfig.BIRTHDAY_QUEUE)
	public void issueCoupon(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(CustomerNotFoundException::new);

		String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("M월"));
		String birthCouponName = currentMonth + " 생일 쿠폰";

		Coupon birthdayCoupon = couponRepository
			.findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc(birthCouponName, true)
			.orElseThrow(() -> new CouponNotFoundException("활성화된 생일 쿠폰이 존재하지 않습니다."));

		MemberCoupon memberCoupon = new MemberCoupon(
			member,
			birthdayCoupon,
			LocalDateTime.now(),
			LocalDateTime.of(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()), LocalTime.MAX)
		);
		memberCouponRepository.save(memberCoupon);
	}
}
