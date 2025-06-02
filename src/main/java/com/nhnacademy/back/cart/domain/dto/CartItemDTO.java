package com.nhnacademy.back.cart.domain.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO implements Serializable {
	private long productId;
	private String productTitle;
	private long productSalePrice;
	private String productImagePath;
	private int cartItemsQuantity;
}
