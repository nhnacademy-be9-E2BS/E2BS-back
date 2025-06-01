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
public class ResponseCartItemsForGuestDTO {

	private long productId;

	private String productTitle;

	private long productSalePrice;

	private String productImagePath;

	private int cartItemsQuantity;

	private long productTotalPrice;

}
