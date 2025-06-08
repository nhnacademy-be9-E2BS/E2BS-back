package com.nhnacademy.back.review.domain.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 마이페이지 리뷰 응답 DTO")
public class ResponseMemberReviewDTO {

	@Schema(description = "리뷰 ID", example = "101")
	private long reviewId;

	@Schema(description = "상품 ID", example = "501")
	private long productId;

	@Schema(description = "상품 썸네일 이미지 URL", example = "/images/products/thumb_501.jpg")
	private String productThumbnail;

	@Schema(description = "상품 제목", example = "무선 이어폰 X200")
	private String productTitle;

	@Schema(description = "리뷰 내용", example = "음질이 좋고 배터리도 오래가요.")
	private String reviewContent;

	@Schema(description = "리뷰 평점 (1~5)", example = "4")
	private int reviewGrade;

	@Schema(description = "리뷰 이미지 URL", example = "/uploads/reviews/123_review.jpg")
	private String reviewImage;

	@Schema(description = "리뷰 작성 일시", example = "2025-06-05T14:32:00")
	private LocalDateTime reviewCreatedAt;

}
