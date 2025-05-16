package com.nhnacademy.back.product.product.domain.dto.response;

import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseProductCouponDTO {
	/**
	 * 상품에 대한 쿠폰을 발급하기 위해 보내주는 정보
	 */
	private long productId;
	private String productTitle;
	private String publisherName;
	private String contributorName;
}
