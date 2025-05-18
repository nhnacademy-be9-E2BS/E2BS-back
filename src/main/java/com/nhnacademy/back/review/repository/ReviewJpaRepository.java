package com.nhnacademy.back.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.review.domain.entity.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
	Page<Review> findAllByCustomer_CustomerId(long customerCustomerId, Pageable pageable);

	Page<Review> findAllByProduct_ProductId(long productProductId, Pageable pageable);
}
