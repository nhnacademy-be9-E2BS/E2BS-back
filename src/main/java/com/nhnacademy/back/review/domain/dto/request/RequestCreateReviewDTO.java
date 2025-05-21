package com.nhnacademy.back.review.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreateReviewDTO {

	@NotNull
	private long productId;

	private Long customerId;

	private String memberId;

	private String reviewContent;

	@NotNull
	int reviewGrade;

	private String reviewImage;

}
