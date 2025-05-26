package com.nhnacademy.back.product.like.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLikedProductDTO {

	private long productId;

	private String productTitle;

	private long productSalePrice;

	private String productThumbnail;

	private long likeCount;

}
