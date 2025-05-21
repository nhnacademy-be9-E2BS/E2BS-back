package com.nhnacademy.back.product.category.domain.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResponseSideBarCategoryDTO {
	@NotNull
	private long categoryId;

	@NotNull
	private String categoryName;
}
