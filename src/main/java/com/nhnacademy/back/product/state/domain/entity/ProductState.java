package com.nhnacademy.back.product.state.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductState {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productStateId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductStateName productStateName;

	public ProductState(ProductStateName productStateName) {
		this.productStateName = productStateName;
	}
}
