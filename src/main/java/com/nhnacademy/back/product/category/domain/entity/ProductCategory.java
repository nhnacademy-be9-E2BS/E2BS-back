package com.nhnacademy.back.product.category.domain.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productCategoryId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id")
	@OnDelete(action = OnDeleteAction.CASCADE)    // Category가 삭제되면 이를 참조하고 있는 ProductCategory 자동 삭제
	private Category category;

	public ProductCategory(Product product, Category category) {
		this.product = product;
		this.category = category;
	}

}
