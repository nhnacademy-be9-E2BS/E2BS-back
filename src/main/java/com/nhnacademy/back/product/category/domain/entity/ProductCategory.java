package com.nhnacademy.back.product.category.domain.entity;

import com.nhnacademy.back.product.product.domain.entity.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productCategoryId;

	@Setter
	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

	@Setter
	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id")
	private Category category;

}
