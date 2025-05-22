package com.nhnacademy.back.product.tag.domain.entity;

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
public class ProductTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productTagId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(optional = false)
	@JoinColumn(name = "tag_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Tag tag;

	public ProductTag(Product product, Tag tag) {
		this.product = product;
		this.tag = tag;
	}
}
