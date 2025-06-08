package com.nhnacademy.back.product.product.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMainPageProductDTO {
	private long productId;
	private String productTitle;
	private String contributorName;
	private String productImage;
	private long productRegularPrice;
	private long productSalePrice;
	private String productDescription;
	private String publisherName;
}

