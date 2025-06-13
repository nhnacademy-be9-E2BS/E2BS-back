package com.nhnacademy.back.event;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.exception.CouponNotFoundException;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;
import com.nhnacademy.back.event.event.WelcomeCouponEvent;
import com.nhnacademy.back.event.listener.WelcomeCouponEventListener;

@ExtendWith(MockitoExtension.class)
class WelcomeCouponEventListenerTest {

	@InjectMocks
	private WelcomeCouponEventListener welcomeCouponEventListener;

	@Mock
	private MemberJpaRepository memberRepository;

	@Mock
	private CouponJpaRepository couponRepository;

	@Mock
	private MemberCouponJpaRepository memberCouponRepository;

	@Test
	void handleWelcomeCouponEvent_success() {
		// Given
		String memberId = "user123";
		Member mockMember = mock(Member.class);
		Coupon mockCoupon = mock(Coupon.class);

		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);
		when(couponRepository.findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc("웰컴 쿠폰", true))
			.thenReturn(Optional.of(mockCoupon));

		WelcomeCouponEvent event = new WelcomeCouponEvent(memberId);

		// When
		welcomeCouponEventListener.handleWelcomeCouponEvent(event);

		// Then
		verify(memberRepository).getMemberByMemberId(memberId);
		verify(couponRepository).findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc("웰컴 쿠폰", true);
		verify(memberCouponRepository).save(any(MemberCoupon.class));
	}

	@Test
	void handleWelcomeCouponEvent_couponNotFound_shouldThrowException() {
		// Given
		String memberId = "user123";
		Member mockMember = mock(Member.class);

		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);
		when(couponRepository.findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc("웰컴 쿠폰", true))
			.thenReturn(Optional.empty());

		WelcomeCouponEvent event = new WelcomeCouponEvent(memberId);

		// When / Then
		Assertions.assertThrows(CouponNotFoundException.class, () ->
			welcomeCouponEventListener.handleWelcomeCouponEvent(event)
		);

		verify(memberRepository).getMemberByMemberId(memberId);
		verify(couponRepository).findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc("웰컴 쿠폰", true);
		verify(memberCouponRepository, never()).save(any());
	}
}
