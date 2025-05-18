package com.nhnacademy.back.cart.domain.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO implements Serializable {
	private long productId;
	private List<ProductCategoryDTO> categoryIds;
	private String productTitle;
	private long productSalePrice;
	private String productImagePath;
	private int cartItemsQuantity;
}
