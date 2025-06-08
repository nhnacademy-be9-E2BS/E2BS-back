package com.nhnacademy.back.review.domain.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 요약 정보 응답 DTO")
public class ResponseReviewInfoDTO {

	@Schema(description = "전체 평점 평균", example = "4.3")
	private double totalGradeAvg;

	@Schema(description = "전체 리뷰 개수", example = "150")
	private int totalCount;

	@Schema(description = "별점별 리뷰 개수 리스트 (0~5점까지 인덱스별 개수)", example = "[5, 10, 20, 40, 50, 25]")
	private List<Integer> starCounts;

}