package com.nhnacademy.back.cart.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAddCartItemsDTO {

	private Long customerId;

	private String sessionId;

	@NotNull
	private long productId;

	@NotNull
	private int quantity;

}
