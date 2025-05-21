package com.nhnacademy.back.coupon.membercoupon.domain.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관리자가 모든 회원에게 쿠폰을 발급할때 사용하는 DTO
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAllMemberCouponDTO {
	@NotNull
	private Long couponId;
	@NotNull
	private LocalDateTime memberCouponPeriod;
}
