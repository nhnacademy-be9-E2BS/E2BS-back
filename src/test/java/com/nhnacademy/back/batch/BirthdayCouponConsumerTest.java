package com.nhnacademy.back.batch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.batch.birthday.BirthdayCouponConsumer;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.exception.CouponNotFoundException;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;

@ExtendWith(MockitoExtension.class)
class BirthdayCouponConsumerTest {

	@Mock
	private MemberJpaRepository memberRepository;
	@Mock
	private CouponJpaRepository couponRepository;
	@Mock
	private MemberCouponJpaRepository memberCouponRepository;
	@InjectMocks
	private BirthdayCouponConsumer consumer;

	@Test
	void issueCoupon_success() {
		// given
		Long memberId = 1L;
		Member member = mock(Member.class);
		Coupon coupon = mock(Coupon.class);

		String monthName = LocalDate.now().getMonthValue() + "월";
		String couponName = monthName + " 생일 쿠폰";

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(couponRepository.findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc(couponName, true))
			.thenReturn(Optional.of(coupon));

		// when
		assertDoesNotThrow(() -> consumer.issueCoupon(memberId));

		// then
		verify(memberRepository).findById(memberId);
		verify(couponRepository).findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc(couponName, true);
		verify(memberCouponRepository).save(any());
	}

	@Test
	void issueCoupon_memberNotFound() {
		Long memberId = 1L;

		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

		assertThrows(CustomerNotFoundException.class, () -> consumer.issueCoupon(memberId));

		verify(memberRepository).findById(memberId);
		verifyNoMoreInteractions(couponRepository, memberCouponRepository);
	}

	@Test
	void issueCoupon_couponNotFound() {
		Long memberId = 1L;
		Member member = mock(Member.class);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

		String couponName = LocalDate.now().getMonthValue() + "월 생일 쿠폰";
		when(couponRepository.findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc(couponName, true))
			.thenReturn(Optional.empty());

		assertThrows(CouponNotFoundException.class, () -> consumer.issueCoupon(memberId));

		verify(memberRepository).findById(memberId);
		verify(couponRepository).findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc(couponName, true);
		verifyNoMoreInteractions(memberCouponRepository);
	}
}
