package com.nhnacademy.back.cart.domain.dto.request;

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

	private String memberId;

	private String sessionId;

	@NotNull
	private Long productId;

	@NotNull
	private Integer quantity;

}
