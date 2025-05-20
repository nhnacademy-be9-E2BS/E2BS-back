package com.nhnacademy.back.order.order.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestOrderDetailDTO {
	@NotNull
	private long productId;
	@NotNull
	private String orderCode;
	@NotNull
	private long wrapperId;
	@NotNull
	private int orderQuantity;
	@NotNull
	private long orderDetailPerPrice;
}
