package com.nhnacademy.back.cart.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MergeCartItemDTO {

	@NotBlank
	private Long productId;

	@NotNull
	private Integer cartQuantity;

}
