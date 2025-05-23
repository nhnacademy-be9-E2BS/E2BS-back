package com.nhnacademy.back.coupon.membercoupon.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseMypageMemberCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseOrderCouponDTO;

public interface MemberCouponService {

	ResponseMypageMemberCouponDTO getMemberCouponCnt(String memberId);

	/**
	 * 회원 ID로 쿠폰 조회
	 */
	Page<ResponseMemberCouponDTO> getMemberCouponsByMemberId(String memberId, Pageable pageable);

	/**
	 * 회원이 쿠폰 사용 시 사용여부 업데이트 (미사용 -> 사용완료)
	 */
	void updateMemberCouponById(Long memberCouponId);

	/**
	 * 주문 취소 시 회원이 사용한 쿠폰과 동일한 내용의 쿠폰을 재발급
	 */
	void reIssueCouponById(Long memberCouponId);

	/**
	 * memberID 에 해당하는 유저가 (보유중 && 미사용 && 상품에 적용 가능한) 쿠폰 리스트 조회
	 */
	List<ResponseOrderCouponDTO> getCouponsInOrderByMemberIdAndProductIds(String memberId, List<Long> productIds);
}
