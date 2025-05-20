package com.nhnacademy.back.review.domain.entity;

import java.time.LocalDateTime;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.review.domain.dto.request.RequestCreateReviewDTO;
import com.nhnacademy.back.review.domain.dto.request.RequestUpdateReviewDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long reviewId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Column(columnDefinition = "TEXT")
	private String reviewContent;

	@Column(nullable = false)
	private int reviewGrade;

	private String reviewImage;

	@Column(nullable = false)
	private LocalDateTime reviewCreatedAt;

	public static Review createReviewEntity(Product product, Customer customer, RequestCreateReviewDTO request) {
		return Review.builder()
			.product(product)
			.customer(customer)
			.reviewContent(request.getReviewContent())
			.reviewGrade(request.getReviewGrade())
			.reviewImage(request.getReviewImage())
			.reviewCreatedAt(LocalDateTime.now())
			.build();

	}

	public void changeReview(RequestUpdateReviewDTO request) {
		this.reviewContent = request.getReviewContent();
		this.reviewGrade = request.getReviewGrade();
		this.reviewImage = request.getReviewImage();
	}
}
