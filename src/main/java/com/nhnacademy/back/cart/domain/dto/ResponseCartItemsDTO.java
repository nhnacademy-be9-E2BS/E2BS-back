package com.nhnacademy.back.cart.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCartItemsDTO {
	private long cartItemId;
	private long productId;
	private String productTitle;
	private long productSalePrice;
	private String productImagePath;
	private int cartItemsQuantity;
}
