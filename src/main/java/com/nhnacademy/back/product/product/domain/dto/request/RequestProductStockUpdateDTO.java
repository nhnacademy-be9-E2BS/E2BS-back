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

	private long productId;
	private int productStock;
}
