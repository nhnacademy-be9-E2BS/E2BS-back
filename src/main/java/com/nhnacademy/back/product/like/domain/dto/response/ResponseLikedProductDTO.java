package com.nhnacademy.back.product.like.domain.dto.response;

import java.time.LocalDateTime;

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

	private String publisherName;

	private String productThumbnail;

	private boolean liked;

	private long likeCount;

	private double avgRating;

	private LocalDateTime likeCreatedAt;
	
}
