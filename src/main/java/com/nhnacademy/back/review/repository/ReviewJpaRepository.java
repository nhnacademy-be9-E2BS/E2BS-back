package com.nhnacademy.back.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.review.domain.entity.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
}
