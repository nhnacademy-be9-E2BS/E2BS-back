package com.nhnacademy.back.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;
import com.nhnacademy.back.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewRestController {

	private final ReviewService reviewService;


	@PostMapping("/api/reviews")
	public ResponseEntity<Void> createReview(@Validated @RequestBody RequestCreateReviewDTO request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		reviewService.createReview(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}


	@PutMapping("/api/reviews/{reviewId}")
	public ResponseEntity<Void> updateReview(@PathVariable long reviewId, @Validated @RequestBody RequestUpdateReviewDTO request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		reviewService.updateReview(reviewId, request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/reviews/{reviewId}")
	public ResponseEntity<Void> deleteReview(@PathVariable long reviewId) {
		reviewService.deleteReview(reviewId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/api/customers/{customerId}/reviews")
	public ResponseEntity<Page<ResponseReviewPageDTO>> getReviewsByCustomer(@PathVariable long customerId, Pageable pageable) {
		Page<ResponseReviewPageDTO> body = reviewService.getReviewsByCustomer(customerId, pageable);
		return ResponseEntity.ok(body);
	}

	@GetMapping("/api/products/{productId}/reviews")
	public ResponseEntity<Page<ResponseReviewPageDTO>> getReviewsByProduct(@PathVariable long productId, Pageable pageable) {
		Page<ResponseReviewPageDTO> body = reviewService.getReviewsByProduct(productId, pageable);
		return ResponseEntity.ok(body);
	}

}
