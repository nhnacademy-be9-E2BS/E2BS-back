package com.nhnacademy.back.product.like.domain.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원이 좋아요한 상품 정보 응답 DTO")
public class ResponseLikedProductDTO {

	@Schema(description = "상품 ID", example = "1001")
	private long productId;

	@Schema(description = "상품 제목", example = "스프링 입문 - 코드로 배우는 스프링 부트")
	private String productTitle;

	@Schema(description = "상품 판매 가격", example = "15000")
	private long productSalePrice;

	@Schema(description = "출판사 이름", example = "한빛미디어")
	private String publisherName;

	@Schema(description = "상품 썸네일 이미지 경로", example = "/images/products/1001_thumbnail.jpg")
	private String productThumbnail;

	@Schema(description = "상품의 총 좋아요 수", example = "58")
	private long likeCount;

	@Schema(description = "상품의 평균 평점", example = "4.7")
	private double avgRating;

	@Schema(description = "리뷰 수", example = "12", defaultValue = "0")
	private int reviewCount = 0;

	@Schema(description = "좋아요 등록 시각", example = "2024-06-01T12:34:56")
	private LocalDateTime likeCreatedAt;

}

