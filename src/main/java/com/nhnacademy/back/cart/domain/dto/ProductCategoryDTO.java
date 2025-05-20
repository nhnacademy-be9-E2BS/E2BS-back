package com.nhnacademy.back.cart.domain.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDTO implements Serializable {
	private long categoryId;
}
