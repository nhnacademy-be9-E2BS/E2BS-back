package com.nhnacademy.back.product.status.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productStatusId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductStatusName productStatusName;

}
