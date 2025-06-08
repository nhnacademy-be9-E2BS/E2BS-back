package com.nhnacademy.back.review.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 작성 요청 메타 DTO")
public class RequestCreateReviewMetaDTO {

	@NotNull
	@Schema(description = "상품 ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long productId;

	@Schema(description = "고객 ID (게스트용)", example = "1001", nullable = true)
	private Long customerId;

	@Schema(description = "회원 ID (로그인 회원용)", example = "member_abc123", nullable = true)
	private String memberId;

	@Schema(description = "리뷰 내용", example = "정말 만족스러운 제품입니다!", nullable = true)
	private String reviewContent;

	@NotNull
	@Schema(description = "리뷰 평점 (1~5)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer reviewGrade;

}
