package com.nhnacademy.back.product.contributor.domain.entity;

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
public class ProductContributor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productContributorId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(optional = false)
	@JoinColumn(name = "contributor_id")
	private Contributor contributor;

	public ProductContributor(Product product, Contributor contributor) {
		this.product = product;
		this.contributor = contributor;
	}

}
