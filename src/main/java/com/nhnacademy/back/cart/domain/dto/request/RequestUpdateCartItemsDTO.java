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
public class RequestUpdateCartItemsDTO {
	private String memberId;
	private String sessionId;
	private Long productId;
	@NotNull
	private Integer quantity;
}
