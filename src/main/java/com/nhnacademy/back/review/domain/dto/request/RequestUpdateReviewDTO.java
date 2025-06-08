package com.nhnacademy.back.review.domain.dto.request;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 수정 요청 DTO")
public class RequestUpdateReviewDTO {

	@Schema(description = "리뷰 내용", example = "정말 만족스러운 제품입니다!", nullable = true)
	private String reviewContent;

	@Schema(description = "리뷰 이미지 파일", type = "string", format = "binary", nullable = true)
	private MultipartFile reviewImage;

}
