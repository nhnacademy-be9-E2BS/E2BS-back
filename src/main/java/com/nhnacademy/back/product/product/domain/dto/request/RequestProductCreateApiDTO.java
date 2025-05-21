package com.nhnacademy.back.product.product.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductCreateApiDTO {
	@NotBlank
	private String publisherName;

	@NotBlank
	private String productTitle;

	@NotBlank
	private String productIsbn;

	@NotBlank
	private String productImage;


	private String productDescription;

	private long productRegularPrice;

	private long productSalePrice;
}
