package com.nhnacademy.back.review.domain.entity;

import java.time.LocalDateTime;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.product.product.domain.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long reviewId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

	// OrderDetail이랑 엮기


	@ManyToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Column(columnDefinition = "TEXT")
	private String reviewContent;

	@Column(nullable = false)
	private int reviewGrade;

	@Column(nullable = false)
	private LocalDateTime reviewCreatedAt;

	private String reviewImage;

}
