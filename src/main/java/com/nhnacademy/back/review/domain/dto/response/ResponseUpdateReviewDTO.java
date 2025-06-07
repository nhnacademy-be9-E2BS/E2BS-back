package com.nhnacademy.back.review.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 수정 응답 DTO")
public class ResponseUpdateReviewDTO {

	@Schema(description = "리뷰 내용", example = "제품이 정말 만족스러웠어요!")
	private String reviewContent;

	@Schema(description = "리뷰 이미지 URL", example = "https://example.com/images/review123.jpg")
	private String reviewImageUrl;

}
