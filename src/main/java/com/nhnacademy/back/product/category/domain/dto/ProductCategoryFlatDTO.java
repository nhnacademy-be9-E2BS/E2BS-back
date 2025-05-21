package com.nhnacademy.back.product.category.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductCategoryFlatDTO {
	private Long productId;
	private Long categoryId;
}
