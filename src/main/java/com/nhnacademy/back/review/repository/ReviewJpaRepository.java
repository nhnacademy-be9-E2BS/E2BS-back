package com.nhnacademy.back.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.back.review.domain.entity.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
	/**
	 * 고객의 리뷰 내역 페이징 조회 메소드
	 */
	Page<Review> findAllByCustomer_CustomerId(long customerCustomerId, Pageable pageable);

	/**
	 * 상품의 리뷰 내역 페이징 조회 메소드
	 */
	Page<Review> findAllByProduct_ProductId(long productProductId, Pageable pageable);

	/**
	 * 상품의 전체 평점 구하는 메소드
	 */
	@Query("select avg(r.reviewGrade) from Review r " +
		   "where r.product.productId = :productId")
	double totalAvgReviewsByProductId(long productId);

	/**
	 * 상품과 리뷰 등급에 해당하는 리뷰 개수 조회 메소드
	 */
	Integer countAllByProduct_ProductIdAndReviewGrade(long productId, int reviewGrade);

	/**
	 * 상품에 해당하는 리뷰 총 개수 조회 메소드
	 */
	Integer countAllByProduct_ProductId(long productProductId);

	/**
	 * 고객이 이미 작성한 리뷰가 있는지 여부 확인 메소드
	 */
	boolean existsByCustomer_CustomerId(long customerCustomerId);
}
