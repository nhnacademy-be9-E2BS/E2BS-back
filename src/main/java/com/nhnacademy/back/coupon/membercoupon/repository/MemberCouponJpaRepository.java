package com.nhnacademy.back.coupon.membercoupon.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseOrderCouponDTO;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;

public interface MemberCouponJpaRepository extends JpaRepository<MemberCoupon, Long> {

	List<MemberCoupon> getMemberCouponsByMemberAndMemberCouponUsed(Member member, boolean memberCouponUsed);

	/**
	 * 쿠폰함 : 회원 ID로 쿠폰 조회
	 */
	Page<MemberCoupon> findByMember_CustomerId(Long customerId, Pageable pageable);

	@Query("""
		    SELECT new com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseOrderCouponDTO(
		        mc.memberCouponId,
		        c.couponName,
		        cp.couponPolicyMinimum,
		        cp.couponPolicyMaximumAmount,
		        cp.couponPolicySalePrice,
		        cp.couponPolicyDiscountRate,
		        cp.couponPolicyName,
		        mc.memberCouponCreatedAt,
		        mc.memberCouponPeriod,
		        NULL,
		        NULL,
		        NULL,
		        NULL
		    )
		    FROM MemberCoupon mc
		    JOIN mc.coupon c
		    JOIN c.couponPolicy cp
		    LEFT JOIN ProductCoupon pc ON c = pc.coupon
		    LEFT JOIN CategoryCoupon cc ON c = cc.coupon
		    WHERE mc.member.customerId = :customerId
		    AND mc.memberCouponUsed = false
		    AND mc.memberCouponPeriod > CURRENT_TIMESTAMP
		    AND pc.coupon IS NULL
		    AND cc.coupon IS NULL
		""")
	List<ResponseOrderCouponDTO> findGeneralCoupons(@Param("customerId") Long customerId);

	@Query("""
		    SELECT new com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseOrderCouponDTO (
		        mc.memberCouponId,
		        c.couponName,
		        cp.couponPolicyMinimum,
		        cp.couponPolicyMaximumAmount,
		        cp.couponPolicySalePrice,
		        cp.couponPolicyDiscountRate,
		        cp.couponPolicyName,
		        mc.memberCouponCreatedAt,
		        mc.memberCouponPeriod,
		        NULL,
		        NULL,
		        p.productId,
		        p.productTitle
		    )
		    FROM MemberCoupon mc
		    JOIN mc.coupon c
		    JOIN c.couponPolicy cp
		    JOIN ProductCoupon pc ON c = pc.coupon
		    JOIN pc.product p
		    WHERE mc.member.customerId = :customerId
		    AND mc.memberCouponUsed = false
		    AND mc.memberCouponPeriod > CURRENT_TIMESTAMP
		    AND p.productId IN :productIds
		""")
	List<ResponseOrderCouponDTO> findProductCoupons(@Param("customerId") Long customerId,
		@Param("productIds") List<Long> productIds);

	@Query("""
		    SELECT new com.nhnacademy.back.coupon.membercoupon.domain.dto.response.ResponseOrderCouponDTO(
		        mc.memberCouponId,
		        c.couponName,
		        cp.couponPolicyMinimum,
		        cp.couponPolicyMaximumAmount,
		        cp.couponPolicySalePrice,
		        cp.couponPolicyDiscountRate,
		        cp.couponPolicyName,
		        mc.memberCouponCreatedAt,
		        mc.memberCouponPeriod,
		        ca.categoryId,
		        ca.categoryName,
		        NULL,
		        NULL
		    )
		    FROM MemberCoupon mc
		    JOIN mc.coupon c
		    JOIN c.couponPolicy cp
		    JOIN CategoryCoupon cc ON c = cc.coupon
		    JOIN cc.category ca
		    JOIN ProductCategory pcat ON ca = pcat.category
		    WHERE mc.member.customerId = :customerId
		    AND mc.memberCouponUsed = false
		    AND mc.memberCouponPeriod > CURRENT_TIMESTAMP
		    AND pcat.product.productId IN :productIds
		""")
	List<ResponseOrderCouponDTO> findCategoryCoupons(@Param("customerId") Long customerId,
		@Param("productIds") List<Long> productIds);

}
