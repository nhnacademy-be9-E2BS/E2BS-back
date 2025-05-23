package com.nhnacademy.back.coupon.membercoupon.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.coupon.coupon.domain.entity.CategoryCoupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.Coupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.ProductCoupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.QCategoryCoupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.QCoupon;
import com.nhnacademy.back.coupon.coupon.domain.entity.QProductCoupon;
import com.nhnacademy.back.coupon.coupon.repository.CategoryCouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.repository.ProductCouponJpaRepository;
import com.nhnacademy.back.coupon.couponpolicy.domain.entity.QCouponPolicy;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.QResponseOrderCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMypageMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseOrderCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.QMemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.exception.MemberCouponUpdateProcessException;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;
import com.nhnacademy.back.product.category.domain.entity.QCategory;
import com.nhnacademy.back.product.category.domain.entity.QProductCategory;
import com.nhnacademy.back.product.product.domain.entity.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCouponServiceImpl implements MemberCouponService {

	private final MemberCouponJpaRepository memberCouponJpaRepository;
	private final CategoryCouponJpaRepository categoryCouponJpaRepository;
	private final ProductCouponJpaRepository productCouponJpaRepository;
	private final MemberJpaRepository memberJpaRepository;
	private final JPAQueryFactory queryFactory;

	/**
	 * 회원 ID로 쿠폰 조회 (쿠폰함)
	 */
	@Override
	public Page<ResponseMemberCouponDTO> getMemberCouponsByMemberId(String memberId, Pageable pageable) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		Long memberCustomerId = member.getCustomerId();

		Page<MemberCoupon> memberCoupons = memberCouponJpaRepository.findByMember_CustomerId(memberCustomerId, pageable);

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

	@Transactional
	public ResponseMypageMemberCouponDTO getMemberCouponCnt(String memberId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException("아이디에 해당하는 회원을 찾지 못했습니다.");
		}

		List<MemberCoupon> memberCoupons = memberCouponJpaRepository.getMemberCouponsByMember(member);
		int couponCnt = 0;
		if (Objects.nonNull(memberCoupons) && !memberCoupons.isEmpty()) {
			couponCnt = memberCoupons.size();
		}

		return new ResponseMypageMemberCouponDTO(memberId, couponCnt);
	}

	@Override
	public List<ResponseOrderCouponDTO> getCouponsInOrderByMemberIdAndProductIds(String memberId, List<Long> productIds) {
		// 1. 회원 조회
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException("아이디에 해당하는 회원을 찾지 못했습니다.");
		}

		// Alias
		QMemberCoupon mc = QMemberCoupon.memberCoupon;
		QCoupon c = QCoupon.coupon;
		QCouponPolicy cp = QCouponPolicy.couponPolicy;
		QProductCoupon pc = QProductCoupon.productCoupon;
		QCategoryCoupon cc = QCategoryCoupon.categoryCoupon;
		QProduct p = QProduct.product;
		QCategory ca = QCategory.category;
		QProductCategory pcat = QProductCategory.productCategory;

		// 공통 조건: 사용여부-false, 만료기한-현재시간보다 이후
		BooleanExpression baseCondition = mc.member.customerId.eq(member.getCustomerId())
			.and(mc.memberCouponUsed.eq(false))
			.and(mc.memberCouponPeriod.after(LocalDateTime.now()));

		// 일반 쿠폰 (상품/카테고리 쿠폰테이블에 없는 전체 할인 쿠폰)
		List<ResponseOrderCouponDTO> generalCoupons = queryFactory
			.select(new QResponseOrderCouponDTO(
				mc.memberCouponId,
				c.couponName,
				cp.couponPolicyMinimum,
				cp.couponPolicyMaximumAmount,
				cp.couponPolicySalePrice,
				cp.couponPolicyDiscountRate,
				cp.couponPolicyName,
				mc.memberCouponCreatedAt,
				mc.memberCouponPeriod,
				Expressions.nullExpression(),   // categoryName
				Expressions.nullExpression()    // productTitle
			))
			.from(mc)
			.join(c).on(mc.coupon.eq(c))
			.join(cp).on(c.couponPolicy.eq(cp))
			.leftJoin(pc).on(c.eq(pc.coupon))
			.leftJoin(cc).on(c.eq(cc.coupon))
			.where(baseCondition
				.and(pc.coupon.isNull())
				.and(cc.coupon.isNull()))
			.fetch();

		// 상품 쿠폰 (상품 쿠폰 테이블에 존재하는 쿠폰)
		List<ResponseOrderCouponDTO> productCoupons = queryFactory
			.select(new QResponseOrderCouponDTO(
				mc.memberCouponId,
				c.couponName,
				cp.couponPolicyMinimum,
				cp.couponPolicyMaximumAmount,
				cp.couponPolicySalePrice,
				cp.couponPolicyDiscountRate,
				cp.couponPolicyName,
				mc.memberCouponCreatedAt,
				mc.memberCouponPeriod,
				Expressions.nullExpression(), // categoryName
				p.productTitle
			))
			.from(mc)
			.join(c).on(mc.coupon.eq(c))
			.join(cp).on(c.couponPolicy.eq(cp))
			.join(pc).on(c.eq(pc.coupon))
			.join(p).on(pc.product.eq(p))
			.where(baseCondition
				.and(pc.product.productId.in(productIds)))
			.fetch();

		// 카테고리 쿠폰 (카테고리 쿠폰 테이블에 존재하는 쿠폰)
		List<ResponseOrderCouponDTO> categoryCoupons = queryFactory
			.select(new QResponseOrderCouponDTO(
				mc.memberCouponId,
				c.couponName,
				cp.couponPolicyMinimum,
				cp.couponPolicyMaximumAmount,
				cp.couponPolicySalePrice,
				cp.couponPolicyDiscountRate,
				cp.couponPolicyName,
				mc.memberCouponCreatedAt,
				mc.memberCouponPeriod,
				ca.categoryName,
				Expressions.nullExpression() // productTitle
			))
			.from(mc)
			.join(c).on(mc.coupon.eq(c))
			.join(cp).on(c.couponPolicy.eq(cp))
			.join(cc).on(c.eq(cc.coupon))
			.join(ca).on(cc.category.eq(ca))
			.join(pcat).on(ca.eq(pcat.category))
			.where(baseCondition
				.and(pcat.product.productId.in(productIds)))
			.fetch();

		// 합치기
		List<ResponseOrderCouponDTO> result = new ArrayList<>();
		result.addAll(generalCoupons);
		result.addAll(productCoupons);
		result.addAll(categoryCoupons);

		return result;
	}



}