package com.nhnacademy.back.review.domain.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUpdateReviewDTO {
	@NotBlank
	private String reviewContent;
	private String reviewImageUrl;
}
