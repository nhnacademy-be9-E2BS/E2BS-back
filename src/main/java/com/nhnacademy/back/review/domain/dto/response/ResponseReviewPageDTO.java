package com.nhnacademy.back.review.domain.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 리뷰 페이징 응답 DTO")
public class ResponseReviewPageDTO {

	@Schema(description = "리뷰 ID", example = "12345")
	private long reviewId;

	@Schema(description = "상품 ID", example = "67890")
	private long productId;

	@Schema(description = "고객 ID", example = "112233")
	private long customerId;

	@Schema(description = "고객 이름", example = "홍길동")
	private String customerName;

	@Schema(description = "리뷰 내용", example = "제품이 정말 만족스러웠어요!")
	private String reviewContent;

	@Schema(description = "리뷰 평점", example = "5")
	private int reviewGrade;

	@Schema(description = "리뷰 이미지 URL", example = "https://example.com/images/review123.jpg")
	private String reviewImage;

	@Schema(description = "리뷰 작성 일시", example = "2024-04-25T14:30:00")
	private LocalDateTime reviewCreatedAt;

}
