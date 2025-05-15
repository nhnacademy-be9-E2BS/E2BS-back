package com.nhnacademy.back.product.category.domain.dto.response;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResponseCategoryIdsDTO {
	@NotNull
	private long productId;

	@NotNull
	private List<Long> categoryIds;
}
