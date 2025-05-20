package com.nhnacademy.back.coupon.membercoupon.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.coupon.coupon.domain.entity.CategoryCoupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.ProductCoupon;
import com.nhnacademy.back.coupon.coupon.repository.CategoryCouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.repository.ProductCouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.exception.MemberCouponUpdateProcessException;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCouponServiceImpl implements MemberCouponService {

	private final MemberCouponJpaRepository memberCouponJpaRepository;
	private final CategoryCouponJpaRepository categoryCouponJpaRepository;
	private final ProductCouponJpaRepository productCouponJpaRepository;

	/**
	 * 회원 ID로 쿠폰 조회 (쿠폰함)
	 */
	@Override
	public Page<ResponseMemberCouponDTO> getMemberCouponsByMemberId(Long memberId, Pageable pageable) {
		Page<MemberCoupon> memberCoupons = memberCouponJpaRepository.findByMember_CustomerId(memberId, pageable);

		return memberCoupons.map(memberCoupon -> {
			Coupon coupon = memberCoupon.getCoupon();

			// null-safe 조회
			CategoryCoupon categoryCoupon = categoryCouponJpaRepository.findById(coupon.getCouponId()).orElse(null);
			ProductCoupon productCoupon = productCouponJpaRepository.findById(coupon.getCouponId()).orElse(null);

			// 카테고리 ID, 이름 조회
			Long categoryId = categoryCoupon != null ? categoryCoupon.getCategory().getCategoryId() : null;
			String categoryName = categoryCoupon != null ? categoryCoupon.getCategory().getCategoryName() : null;

			// 상품 ID, 이름 조회
			Long productId = productCoupon != null ? productCoupon.getProduct().getProductId() : null;
			String productTitle = productCoupon != null ? productCoupon.getProduct().getProductTitle() : null;

			return new ResponseMemberCouponDTO(
				coupon.getCouponId(),
				coupon.getCouponName(),
				coupon.getCouponPolicy().getCouponPolicyName(),
				categoryId,
				categoryName,
				productId,
				productTitle,
				memberCoupon.getMemberCouponCreatedAt(),
				memberCoupon.getMemberCouponPeriod(),
				memberCoupon.isMemberCouponUsed()
			);
		});
	}

	/**
	 * 쿠폰 사용여부 업데이트
	 */
	@Override
	@Transactional
	public void updateMemberCouponById(Long memberCouponId) {
		MemberCoupon memberCoupon = memberCouponJpaRepository.findById(memberCouponId)
			.orElseThrow(() -> new MemberCouponUpdateProcessException("해당 쿠폰을 찾을 수 없습니다. ID: " + memberCouponId));

		memberCoupon.setMemberCouponUsed(true);
		memberCouponJpaRepository.save(memberCoupon);
	}

	/**
	 * 쿠폰 재발급
	 */
	@Override
	@Transactional
	public void reIssueCouponById(Long memberCouponId) {
		MemberCoupon memberCoupon = memberCouponJpaRepository.findById(memberCouponId)
			.orElseThrow(() -> new MemberCouponUpdateProcessException("해당 쿠폰을 찾을 수 없습니다. ID: " + memberCouponId));

		MemberCoupon newMemberCoupon = new MemberCoupon(
			memberCoupon.getMember(),
			memberCoupon.getCoupon(),
			LocalDateTime.now(),
			memberCoupon.getMemberCouponPeriod()
		);

		memberCouponJpaRepository.save(newMemberCoupon);
	}

}
