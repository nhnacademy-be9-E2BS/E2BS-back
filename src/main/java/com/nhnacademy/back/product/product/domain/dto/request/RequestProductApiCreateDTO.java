package com.nhnacademy.back.product.product.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductApiCreateDTO {
	@NotBlank
	private String publisherName;

	@NotBlank
	private String productTitle;

	@NotBlank
	private String productIsbn;

	@NotBlank
	private String productImage;

	@NotBlank
	private String productDescription;

	@NotNull
	private long productRegularPrice;

	@NotNull
	private long productSalePrice;

	@NotBlank
	private String contributors;

}
