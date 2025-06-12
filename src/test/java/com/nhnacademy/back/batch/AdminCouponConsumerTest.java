package com.nhnacademy.back.batch;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.batch.admin.AdminCouponConsumer;
import com.nhnacademy.back.batch.admin.AdminIssueMessage;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.exception.CouponNotFoundException;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;

class AdminCouponConsumerTest {

	private MemberJpaRepository memberRepository;
	private CouponJpaRepository couponRepository;
	private MemberCouponJpaRepository memberCouponRepository;
	private AdminCouponConsumer consumer;

	@BeforeEach
	void setUp() {
		memberRepository = mock(MemberJpaRepository.class);
		couponRepository = mock(CouponJpaRepository.class);
		memberCouponRepository = mock(MemberCouponJpaRepository.class);

		consumer = new AdminCouponConsumer(memberRepository, couponRepository, memberCouponRepository);
	}

	@Test
	void issueCoupon_success() {
		// given
		Long memberId = 1L;
		Long couponId = 10L;
		LocalDateTime expireAt = LocalDateTime.now().plusDays(30);

		AdminIssueMessage message = new AdminIssueMessage(memberId, couponId, expireAt);

		Member member = mock(Member.class);
		Coupon coupon = mock(Coupon.class);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

		// when
		consumer.issueCoupon(message);

		// then
		verify(memberCouponRepository).save(any());
	}

	@Test
	void issueCoupon_memberNotFound_throwsException() {
		// given
		Long memberId = 1L;
		Long couponId = 10L;
		AdminIssueMessage message = new AdminIssueMessage(memberId, couponId, LocalDateTime.now());

		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

		// when + then
		assertThatThrownBy(() -> consumer.issueCoupon(message))
			.isInstanceOf(CustomerNotFoundException.class);
	}

	@Test
	void issueCoupon_couponNotFound_throwsException() {
		// given
		Long memberId = 1L;
		Long couponId = 10L;
		AdminIssueMessage message = new AdminIssueMessage(memberId, couponId, LocalDateTime.now());

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));
		when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

		// when + then
		assertThatThrownBy(() -> consumer.issueCoupon(message))
			.isInstanceOf(CouponNotFoundException.class);

		verify(memberRepository).findById(memberId);
		verify(couponRepository).findById(couponId);
	}

	@Test
	void issueCoupon_unexpectedException_throwsGeneric() {
		// given
		Long memberId = 1L;
		Long couponId = 10L;
		AdminIssueMessage message = new AdminIssueMessage(memberId, couponId, LocalDateTime.now());

		when(memberRepository.findById(memberId)).thenThrow(new RuntimeException("DB error"));

		// when + then
		assertThatThrownBy(() -> consumer.issueCoupon(message))
			.isInstanceOf(RuntimeException.class);

		verify(memberRepository).findById(memberId);
	}
}
