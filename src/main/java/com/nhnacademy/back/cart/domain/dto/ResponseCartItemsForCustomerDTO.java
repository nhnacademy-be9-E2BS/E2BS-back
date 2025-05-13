package com.nhnacademy.back.cart.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCartItemsForCustomerDTO {

	@NotNull
	private long cartItemId;

	@NotNull
	private long productId;

	@NotBlank
	private String productTitle;

	@NotNull
	private long productSalePrice;

	private String productImagePath;

	@NotNull
	private int cartItemsQuantity;

}
