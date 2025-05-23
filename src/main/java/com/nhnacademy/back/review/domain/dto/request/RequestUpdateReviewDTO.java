package com.nhnacademy.back.review.domain.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateReviewDTO {
	private String reviewContent;
	private MultipartFile reviewImage;
}
