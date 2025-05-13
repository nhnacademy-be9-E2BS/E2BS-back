package com.nhnacademy.back.cart.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
	private long productId;
	private String productTitle;
	private long productSalePrice;
	private String productImagePath;
	private int cartItemsQuantity;
}
