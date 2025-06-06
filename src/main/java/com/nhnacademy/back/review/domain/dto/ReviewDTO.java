package com.nhnacademy.back.review.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
	private long productId;
	private long customerId;
	private long reviewId;
	private String reviewContent;
	private int reviewGrade;
	private String reviewImage;
}
