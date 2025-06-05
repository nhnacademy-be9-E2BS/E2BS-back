package com.nhnacademy.back.review.domain.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMemberReviewDTO {

	private long reviewId;
	private long productId;
	private String productThumbnail;
	private String productTitle;
	private String reviewContent;
	private int reviewGrade;
	private String reviewImage;
	private LocalDateTime reviewCreatedAt;

}
