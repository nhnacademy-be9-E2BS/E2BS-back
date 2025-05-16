package com.nhnacademy.back.coupon.coupon.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.common.exception.BadRequestException;
import com.nhnacademy.back.coupon.coupon.domain.dto.request.RequestCouponDTO;
import com.nhnacademy.back.coupon.coupon.domain.dto.response.ResponseCouponDTO;
import com.nhnacademy.back.coupon.coupon.domain.entity.CategoryCoupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.ProductCoupon;
import com.nhnacademy.back.coupon.coupon.exception.CouponNotFoundException;
import com.nhnacademy.back.coupon.coupon.repository.CategoryCouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.repository.CouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.repository.ProductCouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.service.CouponService;
import com.nhnacademy.back.coupon.couponpolicy.domain.entity.CouponPolicy;
import com.nhnacademy.back.coupon.couponpolicy.exception.CouponPolicyNotFoundException;
import com.nhnacademy.back.coupon.couponpolicy.repository.CouponPolicyJpaRepository;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

	private final CouponPolicyJpaRepository couponPolicyJpaRepository;
	private final CouponJpaRepository couponJpaRepository;
	private final CategoryCouponJpaRepository categoryCouponJpaRepository;
	private final ProductCouponJpaRepository productCouponJpaRepository;
	private final CategoryJpaRepository categoryJpaRepository;
	private final ProductJpaRepository productJpaRepository;

	/**
	 * 관리자가 쿠폰을 생성
	 * 카테고리 쿠폰일 경우 categoryCoupon 테이블에도 추가
	 * 상품 쿠폰일 경우 productCoupon 테이블에도 추가
	 */
	@Override
	public void createCoupon(RequestCouponDTO request) {
		CouponPolicy couponPolicy = couponPolicyJpaRepository.findById(request.getCouponPolicyId())
			.orElseThrow(()-> new CouponPolicyNotFoundException("존재하지 않는 쿠폰 정책입니다"));

		Coupon coupon = new Coupon(couponPolicy, request.getCouponName());
		couponJpaRepository.save(coupon);

		if(request.getCategoryId() != null) {
			Category category = categoryJpaRepository.findById(request.getCategoryId())
				.orElseThrow(); // todo CategoryNotFoundException 추가 예정
			CategoryCoupon categoryCoupon = new CategoryCoupon(coupon, category);
			categoryCouponJpaRepository.save(categoryCoupon);
		}
		else if(request.getProductId() != null) {
			Product product = productJpaRepository.findById(request.getProductId())
				.orElseThrow(); // todo ProductNotFoundException 추가 예정
			ProductCoupon productCoupon = new ProductCoupon(coupon, product);
			productCouponJpaRepository.save(productCoupon);
		}

	}

	/**
	 * 쿠폰 전체 조회
	 */
	@Override
	public Page<ResponseCouponDTO> getCoupons(Pageable pageable) {
		Page<Coupon> coupons = couponJpaRepository.findAll(pageable);

		return coupons.map(coupon -> {
			CategoryCoupon categoryCoupon = categoryCouponJpaRepository.findById(coupon.getCouponId())
				.orElse(null);

			ProductCoupon productCoupon = productCouponJpaRepository.findById(coupon.getCouponId())
				.orElse(null);

			Long categoryId = null;
			String categoryName = null;
			if (categoryCoupon != null && categoryCoupon.getCategory() != null) {
				categoryId = categoryCoupon.getCategory().getCategoryId();
				categoryName = categoryCoupon.getCategory().getCategoryName();
			}

			Long productId = null;
			String productTitle = null;
			if (productCoupon != null && productCoupon.getProduct() != null) {
				productId = productCoupon.getProduct().getProductId();
				productTitle = productCoupon.getProduct().getProductTitle();
			}

			return new ResponseCouponDTO(
				coupon.getCouponId(),
				coupon.getCouponPolicy().getCouponPolicyId(),
				coupon.getCouponPolicy().getCouponPolicyName(),
				coupon.getCouponName(),
				categoryId,
				categoryName,
				productId,
				productTitle,
				coupon.isCouponIsActive()
			);
		});
	}

	/**
	 * 쿠폰 ID로 쿠폰 조회
	 * categoryID 존재 : 카테고리 쿠폰
	 * productID 존재 : 삼품 쿠폰
	 * 둘 다 없으면 welcome, 생일 쿠폰
	 */
	@Override
	public ResponseCouponDTO getCoupon(Long couponId) {
		Coupon coupon = couponJpaRepository.findById(couponId)
			.orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰입니다."));

		CategoryCoupon categoryCoupon = categoryCouponJpaRepository.findById(coupon.getCouponId())
			.orElse(null);

		ProductCoupon productCoupon = productCouponJpaRepository.findById(coupon.getCouponId())
			.orElse(null);

		Long categoryId = null;
		String categoryName = null;
		if (categoryCoupon != null && categoryCoupon.getCategory() != null) {
			categoryId = categoryCoupon.getCategory().getCategoryId();
			categoryName = categoryCoupon.getCategory().getCategoryName();
		}

		Long productId = null;
		String productTitle = null;
		if (productCoupon != null && productCoupon.getProduct() != null) {
			productId = productCoupon.getProduct().getProductId();
			productTitle = productCoupon.getProduct().getProductTitle();
		}

		return new ResponseCouponDTO(
			coupon.getCouponId(),
			coupon.getCouponPolicy().getCouponPolicyId(),
			coupon.getCouponPolicy().getCouponPolicyName(),
			coupon.getCouponName(),
			categoryId,
			categoryName,
			productId,
			productTitle,
			coupon.isCouponIsActive()
		);
	}

	/**
	 * 쿠폰활성여부를 변경
	 * 활성, 비활성 2가지 상태만 있기 때문에 현재 상태를 다른 상태로 변경
	 */
	@Override
	public void updateCouponIsActive(Long couponId) {
		Coupon coupon = couponJpaRepository.findById(couponId)
			.orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰입니다."));

		coupon.setCouponIsActive(!coupon.isCouponIsActive());
		couponJpaRepository.save(coupon);
	}

	@Override
	public Page<ResponseCouponDTO> getCouponsIsActive(Pageable pageable) {
		Page<Coupon> coupons = couponJpaRepository.findAllByCouponIsActiveTrue(pageable);

		return coupons.map(coupon -> {
			CategoryCoupon categoryCoupon = categoryCouponJpaRepository.findById(coupon.getCouponId())
				.orElse(null);

			ProductCoupon productCoupon = productCouponJpaRepository.findById(coupon.getCouponId())
				.orElse(null);

			Long categoryId = null;
			String categoryName = null;
			if (categoryCoupon != null && categoryCoupon.getCategory() != null) {
				categoryId = categoryCoupon.getCategory().getCategoryId();
				categoryName = categoryCoupon.getCategory().getCategoryName();
			}

			Long productId = null;
			String productTitle = null;
			if (productCoupon != null && productCoupon.getProduct() != null) {
				productId = productCoupon.getProduct().getProductId();
				productTitle = productCoupon.getProduct().getProductTitle();
			}

			return new ResponseCouponDTO(
				coupon.getCouponId(),
				coupon.getCouponPolicy().getCouponPolicyId(),
				coupon.getCouponPolicy().getCouponPolicyName(),
				coupon.getCouponName(),
				categoryId,
				categoryName,
				productId,
				productTitle,
				coupon.isCouponIsActive()
			);
		});
	}

}
