package com.nhnacademy.back.review.service.impl;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewInfoDTO;
import com.nhnacademy.back.review.domain.dto.response.ResponseReviewPageDTO;
import com.nhnacademy.back.review.domain.entity.Review;
import com.nhnacademy.back.review.exception.ReviewNotFoundException;
import com.nhnacademy.back.review.repository.ReviewJpaRepository;
import com.nhnacademy.back.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final CustomerJpaRepository customerRepository;
	private final MemberJpaRepository memberRepository;
	private final ProductJpaRepository productRepository;
	private final ReviewJpaRepository reviewRepository;

	
	/**
	 * 리뷰 생성 메소드
	 */
	@Transactional
	@Override
	public void createReview(RequestCreateReviewDTO request) {
		Customer findCustomer = null;
		// 회원인 경우
		if (Objects.nonNull(request.getMemberId())) {
			Member findMember = memberRepository.getMemberByMemberId(request.getMemberId());
			findCustomer = customerRepository.findById(findMember.getCustomerId())
				.orElseThrow(CustomerNotFoundException::new);
		}
		// 비회원인 경우
		if (Objects.nonNull(request.getCustomerId())) {
			findCustomer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(CustomerNotFoundException::new);
		}

		Product findProduct = productRepository.findById(request.getProductId())
			.orElseThrow(ProductNotFoundException::new);

		Review reviewEntity = Review.createReviewEntity(findProduct, findCustomer, request);
		reviewRepository.save(reviewEntity);
	}

	/**
	 * 리뷰 수정 메소드
	 */
	@Transactional
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
			review.getCustomer().getCustomerName(),
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
			review.getCustomer().getCustomerName(),
			review.getReviewContent(),
			review.getReviewGrade(),
			review.getReviewImage(),
			review.getReviewCreatedAt()
		));
	}

	/**
	 * 상품에 대한 리뷰 정보(전체 평점, 각 별의 리뷰 개수) 가져오는 메소드
	 */
	@Override
	public ResponseReviewInfoDTO getReviewInfo(long productId) {
		double totalGradeAvg = reviewRepository.totalAvgReviewsByProductId(productId);
		totalGradeAvg = Math.round(totalGradeAvg * 10) / 10.0;

		int totalCount = reviewRepository.countAllByProduct_ProductId(productId);

		ArrayList<Integer> starCounts = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			int count = reviewRepository.countAllByProduct_ProductIdAndReviewGrade(productId, i);
			starCounts.add(count);
		}

		return new ResponseReviewInfoDTO(totalGradeAvg, totalCount, starCounts);
	}

}
