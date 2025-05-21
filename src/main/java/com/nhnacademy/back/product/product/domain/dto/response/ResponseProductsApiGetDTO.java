

package com.nhnacademy.back.product.product.domain.dto.response;

import com.nhnacademy.back.product.product.domain.entity.Product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseProductsApiGetDTO {
	@NotBlank
	private String publisherName;

	@NotBlank
	private String productTitle;

	@NotBlank
	private String productIsbn;

	@NotBlank
	private String productImage;

	// 아래는 안보여줌
	private String productDescription;
	//정가
	private long productRegularPrice;
	//판매가
	private long productSalePrice;
	// 이미지

	public static ResponseProductsApiGetDTO from(Product product) {
		return ResponseProductsApiGetDTO.builder()
			.publisherName(product.getPublisher().getPublisherName())
			.productTitle(product.getProductTitle())
			.productIsbn(product.getProductIsbn())
			.productDescription(product.getProductDescription())
			.productRegularPrice(product.getProductRegularPrice())
			.productSalePrice(product.getProductSalePrice())
			.productImage(
				product.getProductImage().isEmpty() ? null : product.getProductImage().getFirst().getProductImagePath()
			)
			.build();
	}


}