package com.nhnacademy.back.review.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateReviewDTO {

	private String reviewContent;

	@NotNull
	private int reviewGrade;

	private String reviewImage;

}
