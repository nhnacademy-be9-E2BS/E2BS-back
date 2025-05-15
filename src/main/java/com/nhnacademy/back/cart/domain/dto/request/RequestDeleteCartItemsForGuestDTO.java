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
public class RequestDeleteCartItemsForGuestDTO {
	@NotNull
	private long productId;
	@NotNull
	private String sessionId;
}
