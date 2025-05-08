package com.nhnacademy.back.coupon.couponpolicy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.coupon.couponpolicy.domain.dto.RequestCouponPolicyDTO;
import com.nhnacademy.back.coupon.couponpolicy.domain.dto.ResponseCouponPolicyDTO;
import com.nhnacademy.back.coupon.couponpolicy.domain.entity.CouponPolicy;
import com.nhnacademy.back.coupon.couponpolicy.exception.CouponPolicyAlreadyExistException;
import com.nhnacademy.back.coupon.couponpolicy.exception.CouponPolicyNotFoundException;
import com.nhnacademy.back.coupon.couponpolicy.repository.CouponPolicyJpaRepository;
import com.nhnacademy.back.coupon.couponpolicy.service.impl.CouponPolicyServiceImpl;

public class CouponPolicyServiceTest {

	@Mock
	private CouponPolicyJpaRepository couponPolicyJpaRepository;

	@InjectMocks
	private CouponPolicyServiceImpl couponPolicyService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	@DisplayName("관리자 쿠폰 정책 추가 성공")
	void createCouponPolicySuccess() {
		// given
		RequestCouponPolicyDTO requestDTO = new RequestCouponPolicyDTO(
			50000L, 5000L, 3000L, 5, LocalDateTime.now(), "Test Coupon Policy"
		);
		when(couponPolicyJpaRepository.existsByCouponPolicyName(anyString())).thenReturn(false);

		// when
		couponPolicyService.createCouponPolicy(requestDTO);

		// then
		verify(couponPolicyJpaRepository, times(1)).existsByCouponPolicyName(anyString());
		verify(couponPolicyJpaRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("관리자 쿠폰 정책 추가 실패")
	void createCouponPolicyFail() {
		// given
		RequestCouponPolicyDTO requestDTO = new RequestCouponPolicyDTO(
			50000L, 5000L, 3000L, 5, LocalDateTime.now(), "Test Coupon Policy"
		);
		when(couponPolicyJpaRepository.existsByCouponPolicyName(anyString())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> couponPolicyService.createCouponPolicy(requestDTO))
			.isInstanceOf(CouponPolicyAlreadyExistException.class);
	}

	@Test
	@DisplayName("관리자 쿠폰 정책 전체 조회 - 페이징")
	void getCouponPolicies_withPaging() {
		// given
		CouponPolicy couponPolicy1 = new CouponPolicy(
			50000L, 5000L, 3000L, 5, LocalDateTime.now(), "Test Coupon Policy1"
		);
		CouponPolicy couponPolicy2 = new CouponPolicy(
			50000L, 5000L, 3000L, 3, LocalDateTime.now(), "Test Coupon Policy2"
		);

		List<CouponPolicy> dummyData = Arrays.asList(couponPolicy1, couponPolicy2);
		Pageable pageable = PageRequest.of(0, 10);
		Page<CouponPolicy> dummyPage = new PageImpl<>(dummyData, pageable, dummyData.size());

		when(couponPolicyJpaRepository.findAll(any(Pageable.class))).thenReturn(dummyPage);

		// when
		Page<ResponseCouponPolicyDTO> result = couponPolicyService.getCouponPolicies(pageable);

		// then
		verify(couponPolicyJpaRepository, times(1)).findAll(any(Pageable.class));
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getCouponPolicyName()).isEqualTo("Test Coupon Policy1");
	}

	@Test
	@DisplayName("관리자 쿠폰 정책 단건 조회 성공")
	void getCouponPolicyByIdSuccess() {
		// given
		CouponPolicy couponPolicy = new CouponPolicy(
			50000L, 5000L, 3000L, 5, LocalDateTime.now(), "Test Coupon Policy"
		);
		when(couponPolicyJpaRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));

		// when
		ResponseCouponPolicyDTO responseCouponPolicyDTO = couponPolicyService.getCouponPolicyById(1L);

		// then
		assertThat(responseCouponPolicyDTO.getCouponPolicyName()).isEqualTo("Test Coupon Policy");
	}

	@Test
	@DisplayName("관리자 쿠폰 정책 단건 조회 실패")
	void getCouponPolicyByIdFail() {
		// given
		when(couponPolicyJpaRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> couponPolicyService.getCouponPolicyById(1L))
			.isInstanceOf(CouponPolicyNotFoundException.class);
	}

}
