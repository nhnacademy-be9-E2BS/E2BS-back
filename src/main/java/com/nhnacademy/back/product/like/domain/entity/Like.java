package com.nhnacademy.back.product.like.domain.entity;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.product.product.domain.entity.Product;

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
public class Like {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long likeId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;


	public static Like createLikeEntity(Product product, Customer customer) {
		return Like.builder()
			.product(product)
			.customer(customer)
			.build();
	}

}
