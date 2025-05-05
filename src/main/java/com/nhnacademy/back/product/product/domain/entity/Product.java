package com.nhnacademy.back.product.product.domain.entity;

import java.time.LocalDate;

import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.status.domain.entity.ProductStatus;

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
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productId;

	@Setter
	@OneToOne(optional = false)
	@JoinColumn(name = "product_status_id")
	private ProductStatus productStatus;

	@Setter
	@ManyToOne(optional = false)
	@JoinColumn(name = "publisher_id")
	private Publisher publisher;

	@Setter
	@Column(length = 30, nullable = false)
	private String productTitle;

	@Setter
	@Column(nullable = false)
	private String productContent;

	@Setter
	@Column(nullable = false, columnDefinition = "TEXT")
	private String productDescription;

	@Setter
	@Column(nullable = false)
	private LocalDate productPublishedAt;

	@Setter
	@Column(length = 20, nullable = false)
	private String productIsbn;

	@Setter
	@Column(nullable = false)
	private long productRegularPrice;

	@Setter
	@Column(nullable = false)
	private long productSalePrice;

	@Setter
	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean productPackageable;

	@Setter
	@Column(nullable = false)
	private int productStock;
}
