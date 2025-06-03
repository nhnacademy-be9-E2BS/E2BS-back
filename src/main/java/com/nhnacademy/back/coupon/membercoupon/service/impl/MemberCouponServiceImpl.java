package com.nhnacademy.back.coupon.membercoupon.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
import com.nhnacademy.back.coupon.coupon.repository.CategoryCouponJpaRepository;
import com.nhnacademy.back.coupon.coupon.repository.ProductCouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMypageMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseOrderCouponDTO;
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
	private final MemberJpaRepository memberJpaRepository;

	/**
	 * 회원 ID로 쿠폰 조회 (쿠폰함)
	 */
	@Override
	public Page<ResponseMemberCouponDTO> getMemberCouponsByMemberId(String memberId, Pageable pageable) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		Long memberCustomerId = member.getCustomerId();

		Page<MemberCoupon> memberCoupons = memberCouponJpaRepository.findByMember_CustomerId(
			memberCustomerId,
			pageable);

		return memberCoupons.map(this::convertToDto);
	}

	@Override
	public Page<ResponseMemberCouponDTO> getUsableMemberCouponsByMemberId(String memberId, Pageable pageable) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		Long memberCustomerId = member.getCustomerId();

		Page<MemberCoupon> memberCoupons =
			memberCouponJpaRepository.findByMember_CustomerIdAndMemberCouponUsedIsFalseAndMemberCouponPeriodGreaterThanEqual(
				memberCustomerId,
				LocalDateTime.now(),
				pageable);

		return memberCoupons.map(this::convertToDto);
	}

	@Override
	public Page<ResponseMemberCouponDTO> getUnusableMemberCouponsByMemberId(String memberId, Pageable pageable) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		Long memberCustomerId = member.getCustomerId();

		Page<MemberCoupon> memberCoupons =
			memberCouponJpaRepository.findByMember_CustomerIdAndMemberCouponUsedIsTrueOrMemberCouponPeriodBefore(
				memberCustomerId,
				LocalDateTime.now(),
				pageable);

		return memberCoupons.map(this::convertToDto);
	}

	// 공통 DTO 변환 메소드
	private ResponseMemberCouponDTO convertToDto(MemberCoupon memberCoupon) {
		Coupon coupon = memberCoupon.getCoupon();

		CategoryCoupon categoryCoupon = categoryCouponJpaRepository.findById(coupon.getCouponId()).orElse(null);
		ProductCoupon productCoupon = productCouponJpaRepository.findById(coupon.getCouponId()).orElse(null);

		Long categoryId = categoryCoupon != null ? categoryCoupon.getCategory().getCategoryId() : null;
		String categoryName = categoryCoupon != null ? categoryCoupon.getCategory().getCategoryName() : null;

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
	public List<ResponseOrderCouponDTO> getCouponsInOrderByMemberIdAndProductIds(String memberId,
		List<Long> productIds) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (member == null) {
			throw new NotFoundMemberException("아이디에 해당하는 회원을 찾지 못했습니다.");
		}

		Long customerId = member.getCustomerId();
		List<ResponseOrderCouponDTO> result = new ArrayList<>();
		result.addAll(memberCouponJpaRepository.findGeneralCoupons(customerId));
		result.addAll(memberCouponJpaRepository.findProductCoupons(customerId, productIds));
		result.addAll(memberCouponJpaRepository.findCategoryCoupons(customerId, productIds));

		// 쿼리 결과, 리스트에서 memberCouponID 를 기준으로 중복을 제거한 후 반환
		return new ArrayList<>(result.stream()
			.collect(Collectors.toMap(
				ResponseOrderCouponDTO::getMemberCouponId,
				dto -> dto,
				(existing, replacement) -> existing
			))
			.values());

	}

}