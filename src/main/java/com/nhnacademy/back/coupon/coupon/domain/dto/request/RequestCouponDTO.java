package com.nhnacademy.back.coupon.coupon.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestCouponDTO {
	@NotNull
	private Long couponPolicyId;
	@NotNull
	@Size(max = 30)
	private String couponName;

	private Long categoryId;
	private Long productId;
}
