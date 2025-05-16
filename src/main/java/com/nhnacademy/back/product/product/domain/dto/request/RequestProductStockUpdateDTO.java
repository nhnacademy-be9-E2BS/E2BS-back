package com.nhnacademy.back.product.product.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductStockUpdateDTO {
	/**
	 * 도서의 재고를 변경하기 위한 정보
	 */
	private long productId;
	private int productDecrementStock;
}
