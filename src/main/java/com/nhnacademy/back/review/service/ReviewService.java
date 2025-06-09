package com.nhnacademy.back.review.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseMemberReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewInfoDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseUpdateReviewDTO;

public interface ReviewService {
	void createReview(RequestCreateReviewDTO request) throws IOException;
	ResponseUpdateReviewDTO updateReview(long reviewId, RequestUpdateReviewDTO request);
	Page<ResponseReviewPageDTO> getReviewsByProduct(long productId, Pageable pageable);
	ResponseReviewInfoDTO getReviewInfo(long productId);
	Page<ResponseMemberReviewDTO> getReviewsByMember(String memberId, Pageable pageable);
	boolean existsReviewedOrderCode(String orderCode);
	ResponseReviewDTO findByOrderDetailId(long orderDetailId);
}
