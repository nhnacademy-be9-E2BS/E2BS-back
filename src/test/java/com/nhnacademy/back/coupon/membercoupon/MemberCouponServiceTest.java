package com.nhnacademy.back.coupon.membercoupon;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.repository.CategoryCouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.repository.ProductCouponJpaRepository;
import com.nhnacademy.back.coupon.couponpolicy.domain.entity.CouponPolicy;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMypageMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseOrderCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.exception.MemberCouponUpdateProcessException;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.service.impl.MemberCouponServiceImpl;

@ExtendWith(MockitoExtension.class)
class MemberCouponServiceTest {

	@InjectMocks
	MemberCouponServiceImpl service;

	@Mock
	MemberCouponJpaRepository memberCouponJpaRepository;

	@Mock
	CategoryCouponJpaRepository categoryCouponJpaRepository;

	@Mock
	ProductCouponJpaRepository productCouponJpaRepository;

	@Mock
	MemberJpaRepository memberJpaRepository;

	@Test
	@DisplayName("회원 쿠폰함 조회")
	void testGetMemberCouponsByMemberId() {
		Member member = mock(Member.class);
		when(member.getCustomerId()).thenReturn(1L);

		CouponPolicy couponPolicy = mock(CouponPolicy.class);
		when(couponPolicy.getCouponPolicyName()).thenReturn("정책A");

		Coupon coupon = mock(Coupon.class);
		when(coupon.getCouponPolicy()).thenReturn(couponPolicy);
		when(coupon.getCouponName()).thenReturn("쿠폰A");
		when(coupon.getCouponId()).thenReturn(10L);

		MemberCoupon memberCoupon = mock(MemberCoupon.class);
		when(memberCoupon.getCoupon()).thenReturn(coupon);
		when(memberCoupon.isMemberCouponUsed()).thenReturn(false);
		when(memberCoupon.getMemberCouponCreatedAt()).thenReturn(LocalDateTime.now().minusDays(1));
		when(memberCoupon.getMemberCouponPeriod()).thenReturn(LocalDateTime.now().plusDays(5));

		Pageable pageable = PageRequest.of(0, 10);

		when(memberJpaRepository.getMemberByMemberId("user1")).thenReturn(member);

		Page<MemberCoupon> memberCouponPage = new PageImpl<>(List.of(memberCoupon), pageable, 1);
		when(memberCouponJpaRepository.findByMember_CustomerId(1L, pageable)).thenReturn(memberCouponPage);

		when(categoryCouponJpaRepository.findById(anyLong())).thenReturn(Optional.empty());
		when(productCouponJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		Page<ResponseMemberCouponDTO> result = service.getMemberCouponsByMemberId("user1", pageable);

		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getCouponName()).isEqualTo("쿠폰A");
	}

	@Test
	@DisplayName("회원 쿠폰 개수 조회 성공")
	void testGetMemberCouponCnt() {
		Member member = mock(Member.class);

		when(memberJpaRepository.getMemberByMemberId("user1")).thenReturn(member);

		MemberCoupon memberCoupon = mock(MemberCoupon.class);
		List<MemberCoupon> coupons = List.of(memberCoupon);
		when(memberCouponJpaRepository.getMemberCouponsByMemberAndMemberCouponUsedAndMemberCouponPeriodAfter(
			any(), eq(false), any())).thenReturn(coupons);

		ResponseMypageMemberCouponDTO dto = service.getMemberCouponCnt("user1");

		assertThat(dto.getMemberId()).isEqualTo("user1");
		assertThat(dto.getCouponCnt()).isEqualTo(1);
	}

	@Test
	@DisplayName("회원 쿠폰 개수 조회 - 회원없음 예외 발생")
	void testGetMemberCouponCntMemberNotFound() {
		when(memberJpaRepository.getMemberByMemberId("user1")).thenReturn(null);

		assertThatThrownBy(() -> service.getMemberCouponCnt("user1"))
			.isInstanceOf(NotFoundMemberException.class)
			.hasMessageContaining("아이디에 해당하는 회원을 찾지 못했습니다.");
	}

	@Test
	@DisplayName("쿠폰 사용여부 업데이트 성공")
	void testUpdateMemberCouponById() {
		MemberCoupon memberCoupon = mock(MemberCoupon.class);

		when(memberCouponJpaRepository.findById(1L)).thenReturn(Optional.of(memberCoupon));

		service.updateMemberCouponById(1L);

		verify(memberCouponJpaRepository).save(memberCoupon);
		verify(memberCoupon).setMemberCouponUsed(true);
	}

	@Test
	@DisplayName("쿠폰 사용여부 업데이트 - 쿠폰 없음 예외")
	void testUpdateMemberCouponByIdNotFound() {
		when(memberCouponJpaRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.updateMemberCouponById(1L))
			.isInstanceOf(MemberCouponUpdateProcessException.class)
			.hasMessageContaining("해당 쿠폰을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("쿠폰 재발급 성공")
	void testReIssueCouponById() {
		MemberCoupon memberCoupon = mock(MemberCoupon.class);

		when(memberCouponJpaRepository.findById(1L)).thenReturn(Optional.of(memberCoupon));

		service.reIssueCouponById(1L);

		verify(memberCouponJpaRepository).save(any(MemberCoupon.class));
	}

	@Test
	@DisplayName("쿠폰 재발급 - 쿠폰 없음 예외")
	void testReIssueCouponByIdNotFound() {
		when(memberCouponJpaRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.reIssueCouponById(1L))
			.isInstanceOf(MemberCouponUpdateProcessException.class)
			.hasMessageContaining("해당 쿠폰을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("주문서 쿠폰 조회 성공")
	void testGetCouponsInOrderByMemberIdAndProductIds() {
		Member member = mock(Member.class);
		when(member.getCustomerId()).thenReturn(1L);

		when(memberJpaRepository.getMemberByMemberId("user1")).thenReturn(member);

		ResponseOrderCouponDTO dto1 = new ResponseOrderCouponDTO();
		dto1.setMemberCouponId(1L);
		ResponseOrderCouponDTO dto2 = new ResponseOrderCouponDTO();
		dto2.setMemberCouponId(2L);

		when(memberCouponJpaRepository.findGeneralCoupons(1L)).thenReturn(List.of(dto1));
		when(memberCouponJpaRepository.findProductCoupons(1L, List.of(100L, 200L))).thenReturn(List.of(dto1, dto2));
		when(memberCouponJpaRepository.findCategoryCoupons(1L, List.of(100L, 200L))).thenReturn(List.of(dto2));

		List<ResponseOrderCouponDTO> result = service.getCouponsInOrderByMemberIdAndProductIds("user1", List.of(100L, 200L));

		// memberCouponId 기준 중복 제거 되므로 결과는 2개
		assertThat(result).hasSize(2);
	}

	@Test
	@DisplayName("주문서 쿠폰 조회 - 회원 없음 예외")
	void testGetCouponsInOrderByMemberIdAndProductIdsMemberNotFound() {
		when(memberJpaRepository.getMemberByMemberId("user1")).thenReturn(null);

		List<Long> productIds = List.of(100L);

		Assertions.assertThrows(NotFoundMemberException.class,
			() -> service.getCouponsInOrderByMemberIdAndProductIds("user1", productIds));
	}
}
