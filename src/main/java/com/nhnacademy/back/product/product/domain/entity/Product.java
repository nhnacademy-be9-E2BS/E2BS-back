package com.nhnacademy.back.product.product.domain.entity;

import java.time.LocalDate;

import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productId;

	@OneToOne(optional = false)
	@JoinColumn(name = "product_state_id")
	private ProductState productState;

	@ManyToOne(optional = false)
	@JoinColumn(name = "publisher_id")
	private Publisher publisher;

	@Column(length = 30, nullable = false)
	private String productTitle;

	@Column(nullable = false)
	private String productContent;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String productDescription;

	@Column(nullable = false)
	private LocalDate productPublishedAt;

	@Column(length = 20, nullable = false)
	private String productIsbn;

	@Column(nullable = false)
	private long productRegularPrice;

	@Column(nullable = false)
	private long productSalePrice;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean productPackageable;

	@Column(nullable = false)
	private int productStock;

	@Column(nullable = false, columnDefinition = "bigint DEFAULT 0")
	private long productHits = 0;

	@Column(nullable = false, columnDefinition = "bigint DEFAULT 0")
	private long productSearches = 0;

}
