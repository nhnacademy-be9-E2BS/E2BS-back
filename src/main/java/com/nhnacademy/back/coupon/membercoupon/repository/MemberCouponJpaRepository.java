package com.nhnacademy.back.coupon.membercoupon.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;

public interface MemberCouponJpaRepository extends JpaRepository<MemberCoupon, Long> {

	List<MemberCoupon> getMemberCouponsByMember(Member member);

	/**
	 * 쿠폰함 : 회원 ID로 쿠폰 조회
	 */
	Page<MemberCoupon> findByMember_CustomerId(Long customerId, Pageable pageable);

	/**
	 * 주문서 : 해당 회원의 보유 쿠폰 중 사용하지 않은 것들
	 */
	List<MemberCoupon> findByMember_CustomerIdAndMemberCouponUsedFalse(Long customerId);

}
