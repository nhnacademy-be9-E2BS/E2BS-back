package com.nhnacademy.back.coupon.coupon;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

import com.nhnacademy.back.coupon.coupon.domain.dto.request.RequestCouponDTO;
import com.nhnacademy.back.coupon.coupon.domain.dto.response.ResponseCouponDTO;
import com.nhnacademy.back.coupon.coupon.domain.entity.CategoryCoupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.ProductCoupon;
import com.nhnacademy.back.coupon.coupon.exception.CouponNotFoundException;
import com.nhnacademy.back.coupon.coupon.repository.*;
import com.nhnacademy.back.coupon.coupon.service.impl.CouponServiceImpl;
import com.nhnacademy.back.coupon.couponpolicy.domain.entity.CouponPolicy;
import com.nhnacademy.back.coupon.couponpolicy.exception.CouponPolicyNotFoundException;
import com.nhnacademy.back.coupon.couponpolicy.repository.CouponPolicyJpaRepository;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

class CouponServiceTest {

	@InjectMocks
	private CouponServiceImpl couponService;

	@Mock
	private CouponPolicyJpaRepository couponPolicyJpaRepository;
	@Mock
	private CouponJpaRepository couponJpaRepository;
	@Mock
	private CategoryCouponJpaRepository categoryCouponJpaRepository;
	@Mock
	private ProductCouponJpaRepository productCouponJpaRepository;
	@Mock
	private CategoryJpaRepository categoryJpaRepository;
	@Mock
	private ProductJpaRepository productJpaRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("카테고리 쿠폰 생성 성공")
	void createCoupon_withCategoryCoupon_success() {
		RequestCouponDTO request = new RequestCouponDTO(1L, "쿠폰1", 10L, null);
		CouponPolicy policy = mock(CouponPolicy.class);
		Category category = mock(Category.class);

		when(couponPolicyJpaRepository.findById(1L)).thenReturn(Optional.of(policy));
		when(categoryJpaRepository.findById(10L)).thenReturn(Optional.of(category));

		couponService.createCoupon(request);

		verify(categoryCouponJpaRepository).save(any(CategoryCoupon.class));
	}

	@Test
	@DisplayName("상품 쿠폰 생성 성공")
	void createCoupon_withProductCoupon_success() {
		RequestCouponDTO request = new RequestCouponDTO(1L, "쿠폰2", null, 20L);
		CouponPolicy policy = mock(CouponPolicy.class);
		Product product = mock(Product.class);

		when(couponPolicyJpaRepository.findById(1L)).thenReturn(Optional.of(policy));
		when(productJpaRepository.findById(20L)).thenReturn(Optional.of(product));

		couponService.createCoupon(request);

		verify(productCouponJpaRepository).save(any(ProductCoupon.class));
	}

	@Test
	@DisplayName("쿠폰 생성 실패 : 존재하지 않는 쿠폰 정책")
	void createCoupon_withInvalidPolicy_shouldThrowException() {
		RequestCouponDTO request = new RequestCouponDTO(99L, "쿠폰X", null, 1L);

		when(couponPolicyJpaRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(CouponPolicyNotFoundException.class, () -> couponService.createCoupon(request));
	}

	@Test
	@DisplayName("쿠폰 전체 조회")
	void getCoupons_success() {
		Pageable pageable = PageRequest.of(0, 10);
		CouponPolicy policy = mock(CouponPolicy.class);
		Coupon coupon = new Coupon(policy, "Test Coupon");
		Page<Coupon> page = new PageImpl<>(List.of(coupon));

		when(couponJpaRepository.findAll(pageable)).thenReturn(page);
		when(categoryCouponJpaRepository.findById(anyLong())).thenReturn(Optional.empty());
		when(productCouponJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		Page<ResponseCouponDTO> result = couponService.getCoupons(pageable);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getCouponName()).isEqualTo("Test Coupon");
	}

	@Test
	@DisplayName("쿠폰 ID로 단건 조회 성공")
	void getCoupon_validId_shouldReturnCouponDTO() {
		CouponPolicy policy = mock(CouponPolicy.class);
		Coupon coupon = new Coupon(policy, "생일 쿠폰");

		when(couponJpaRepository.findById(5L)).thenReturn(Optional.of(coupon));
		when(categoryCouponJpaRepository.findById(5L)).thenReturn(Optional.empty());
		when(productCouponJpaRepository.findById(5L)).thenReturn(Optional.empty());

		ResponseCouponDTO result = couponService.getCoupon(5L);

		assertThat(result.getCouponName()).isEqualTo("생일 쿠폰");
	}

	@Test
	@DisplayName("쿠폰 ID로 단건 조회 실패 : 존재하지 않는 ID")
	void getCoupon_invalidId_shouldThrowException() {
		when(couponJpaRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(CouponNotFoundException.class, () -> couponService.getCoupon(999L));
	}

	@Test
	@DisplayName("쿠폰 활성화 상태 변경 성공")
	void updateCouponIsActive_success() {
		CouponPolicy policy = mock(CouponPolicy.class);
		Coupon coupon = new Coupon(policy, "테스트 쿠폰");
		coupon.setCouponIsActive(false);  // 초기 상태: 비활성

		when(couponJpaRepository.findById(1L)).thenReturn(Optional.of(coupon));

		couponService.updateCouponIsActive(1L);

		assertThat(coupon.isCouponIsActive()).isTrue();  // 활성화됐는지 확인
		verify(couponJpaRepository).save(coupon);
	}

	@Test
	@DisplayName("쿠폰 활성화 상태 변경 실패: 존재하지 않는 쿠폰 ID")
	void updateCouponIsActive_invalidId_shouldThrowException() {
		when(couponJpaRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(CouponNotFoundException.class, () -> couponService.updateCouponIsActive(999L));
	}

}
