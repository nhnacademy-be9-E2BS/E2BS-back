package com.nhnacademy.back.review.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewMetaDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewInfoDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseUpdateReviewDTO;
import com.nhnacademy.back.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewRestController {

	private final ReviewService reviewService;


	@PostMapping(value = "/api/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> createReview(@Validated @RequestPart("requestMeta") RequestCreateReviewMetaDTO requestMeta, BindingResult bindingResult, @RequestPart("reviewImage") MultipartFile reviewImage) throws IOException {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		RequestCreateReviewDTO request = new RequestCreateReviewDTO(requestMeta.getProductId(), requestMeta.getCustomerId(), requestMeta.getMemberId(), requestMeta.getReviewContent(), requestMeta.getReviewGrade(), reviewImage);
		reviewService.createReview(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}


	@PutMapping("/api/reviews/{reviewId}")
	public ResponseEntity<ResponseUpdateReviewDTO> updateReview(@PathVariable long reviewId, @Validated @RequestPart("reviewContent") String reviewContent, BindingResult bindingResult, @RequestPart("reviewImage") MultipartFile reviewImage) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		RequestUpdateReviewDTO request = new RequestUpdateReviewDTO(reviewContent, reviewImage);
		ResponseUpdateReviewDTO body = reviewService.updateReview(reviewId, request);
		return ResponseEntity.ok(body);
	}

	@GetMapping("/api/products/{productId}/reviews")
	public ResponseEntity<Page<ResponseReviewPageDTO>> getReviewsByProduct(@PathVariable long productId, Pageable pageable) {
		Page<ResponseReviewPageDTO> body = reviewService.getReviewsByProduct(productId, pageable);
		return ResponseEntity.ok(body);
	}

	@GetMapping("/api/products/{productId}/reviews/info")
	public ResponseEntity<ResponseReviewInfoDTO> getReviewInfo(@PathVariable long productId) {
		ResponseReviewInfoDTO body = reviewService.getReviewInfo(productId);
		return ResponseEntity.ok(body);
	}

}
