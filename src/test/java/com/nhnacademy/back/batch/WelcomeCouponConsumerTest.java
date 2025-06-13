package com.nhnacademy.back.batch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.batch.welcome.WelcomeCouponConsumer;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.exception.CouponNotFoundException;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;

@ExtendWith(MockitoExtension.class)
class WelcomeCouponConsumerTest {

	@Mock
	private MemberJpaRepository memberRepository;

	@Mock
	private CouponJpaRepository couponRepository;

	@Mock
	private MemberCouponJpaRepository memberCouponRepository;

	@InjectMocks
	private WelcomeCouponConsumer welcomeCouponConsumer;

	@Test
	void issueCoupon_success() {
		Long customerId = 1L;

		Member mockMember = mock(Member.class);
		Coupon mockCoupon = mock(Coupon.class);

		when(memberRepository.findById(customerId)).thenReturn(Optional.of(mockMember));
		when(couponRepository.findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc("웰컴 쿠폰", true))
			.thenReturn(Optional.of(mockCoupon));

		welcomeCouponConsumer.issueCoupon(customerId);

		ArgumentCaptor<MemberCoupon> captor = ArgumentCaptor.forClass(MemberCoupon.class);
		verify(memberCouponRepository).save(captor.capture());

		MemberCoupon savedCoupon = captor.getValue();
		assertEquals(mockMember, savedCoupon.getMember());
		assertEquals(mockCoupon, savedCoupon.getCoupon());
		assertTrue(savedCoupon.getMemberCouponCreatedAt().isBefore(savedCoupon.getMemberCouponPeriod()));
	}

	@Test
	void issueCoupon_customerNotFound_throwsException() {
		Long customerId = 2L;

		when(memberRepository.findById(customerId)).thenReturn(Optional.empty());

		assertThrows(CustomerNotFoundException.class, () -> {
			welcomeCouponConsumer.issueCoupon(customerId);
		});
	}

	@Test
	void issueCoupon_couponNotFound_throwsException() {
		Long customerId = 3L;
		Member mockMember = mock(Member.class);

		when(memberRepository.findById(customerId)).thenReturn(Optional.of(mockMember));
		when(couponRepository.findFirstByCouponNameAndCouponIsActiveOrderByCouponIdDesc("웰컴 쿠폰", true))
			.thenReturn(Optional.empty());

		assertThrows(CouponNotFoundException.class, () -> {
			welcomeCouponConsumer.issueCoupon(customerId);
		});
	}
}
