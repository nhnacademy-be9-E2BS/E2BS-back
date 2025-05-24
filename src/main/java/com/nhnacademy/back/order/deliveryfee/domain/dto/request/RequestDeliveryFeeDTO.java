package com.nhnacademy.back.order.deliveryfee.domain.dto.request;

import jakarta.validation.constraints.NotNull;
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
	private long deliveryFeeAmount;
	@NotNull
	private long deliveryFeeFreeAmount;
}
