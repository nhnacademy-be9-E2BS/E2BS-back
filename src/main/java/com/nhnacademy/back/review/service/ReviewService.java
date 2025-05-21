package com.nhnacademy.back.review.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewInfoDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;

public interface ReviewService {
	void createReview(RequestCreateReviewDTO request) throws IOException;
	void updateReview(long reviewId, RequestUpdateReviewDTO request);
	Page<ResponseReviewPageDTO> getReviewsByCustomer(long customerId, Pageable pageable);
	Page<ResponseReviewPageDTO> getReviewsByProduct(long productId, Pageable pageable);
	ResponseReviewInfoDTO getReviewInfo(long productId);
}
