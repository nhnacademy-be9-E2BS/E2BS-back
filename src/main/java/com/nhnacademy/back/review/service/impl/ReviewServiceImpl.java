package com.nhnacademy.back.review.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;
import com.nhnacademy.back.review.domain.entity.Review;
import com.nhnacademy.back.review.exception.ReviewNotFoundException;
import com.nhnacademy.back.review.repository.ReviewJpaRepository;
import com.nhnacademy.back.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final CustomerJpaRepository customerRepository;
	private final ProductJpaRepository productRepository;
	private final ReviewJpaRepository reviewRepository;

	
	/**
	 * 리뷰 생성 메소드
	 */
	@Override
	public void createReview(RequestCreateReviewDTO request) {
		Customer findCustomer = customerRepository.findById(request.getCustomerId())
			.orElseThrow(CustomerNotFoundException::new);
		Product findProduct = productRepository.findById(request.getProductId())
			.orElseThrow(ProductNotFoundException::new);

		Review reviewEntity = Review.createReviewEntity(findProduct, findCustomer, request);
		reviewRepository.save(reviewEntity);
	}

	/**
	 * 리뷰 수정 메소드
	 */
	@Override
	public void updateReview(long reviewId, RequestUpdateReviewDTO request) {
		Review findReview = reviewRepository.findById(reviewId)
			.orElseThrow(ReviewNotFoundException::new);

		findReview.changeReview(request);
	}

	/**
	 * 고객 리뷰 페이징 목록 조회 메소드
	 */
	@Override
	public Page<ResponseReviewPageDTO> getReviewsByCustomer(long customerId, Pageable pageable) {
		Page<Review> getReviewsByCustomerId = reviewRepository.findAllByCustomer_CustomerId(customerId, pageable);

		return getReviewsByCustomerId.map(review -> new ResponseReviewPageDTO(
			review.getReviewId(),
			review.getProduct().getProductId(),
			review.getCustomer().getCustomerId(),
			review.getReviewContent(),
			review.getReviewGrade(),
			review.getReviewImage(),
			review.getReviewCreatedAt()
		));
	}

	/**
	 * 상품 리뷰 페이징 목록 조회 메소드
	 */
	@Override
	public Page<ResponseReviewPageDTO> getReviewsByProduct(long productId, Pageable pageable) {
		Page<Review> getReviewsByProductId = reviewRepository.findAllByProduct_ProductId(productId, pageable);

		return getReviewsByProductId.map(review -> new ResponseReviewPageDTO(
			review.getReviewId(),
			review.getProduct().getProductId(),
			review.getCustomer().getCustomerId(),
			review.getReviewContent(),
			review.getReviewGrade(),
			review.getReviewImage(),
			review.getReviewCreatedAt()
		));
	}

}
