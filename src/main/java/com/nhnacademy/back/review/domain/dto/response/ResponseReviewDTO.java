package com.nhnacademy.back.review.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 상세 응답 DTO")
public class ResponseReviewDTO {

	@Schema(description = "상품 ID", example = "67890")
	private long productId;

	@Schema(description = "고객 ID", example = "112233")
	private long customerId;

	@Schema(description = "리뷰 ID", example = "12345")
	private long reviewId;

	@Schema(description = "리뷰 내용", example = "제품이 매우 만족스럽습니다.")
	private String reviewContent;

	@Schema(description = "리뷰 평점", example = "4")
	private int reviewGrade;

	@Schema(description = "리뷰 이미지 URL", example = "https://example.com/images/review123.jpg")
	private String reviewImage;
}
