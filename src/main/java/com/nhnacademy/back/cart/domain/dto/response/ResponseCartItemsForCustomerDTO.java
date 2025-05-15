package com.nhnacademy.back.cart.domain.dto.response;

import java.util.List;

import com.nhnacademy.back.cart.domain.dto.ProductCategoryDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCartItemsForCustomerDTO {

	private long cartItemId;

	private long productId;

	private List<ProductCategoryDTO> categoryIds;

	private String productTitle;

	private long productSalePrice;

	private String productImagePath;

	private int cartItemsQuantity;

	private long productTotalPrice;

}
