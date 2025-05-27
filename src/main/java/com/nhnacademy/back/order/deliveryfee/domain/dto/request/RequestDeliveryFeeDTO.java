package com.nhnacademy.back.order.deliveryfee.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDeliveryFeeDTO {
	@NotNull
	@PositiveOrZero
	private long deliveryFeeAmount;
	@NotNull
	@PositiveOrZero
	private long deliveryFeeFreeAmount;
}
