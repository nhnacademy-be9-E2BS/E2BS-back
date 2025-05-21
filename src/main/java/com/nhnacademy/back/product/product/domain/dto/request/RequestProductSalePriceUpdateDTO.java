package com.nhnacademy.back.product.product.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductSalePriceUpdateDTO {
	/**
	 * 도서의 할인가를 변경하기 위한 정보
	 */
	private long productSalePrice;
}
