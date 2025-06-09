package com.nhnacademy.back.order.order.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestOrderReturnDTO {
	@NotNull
	private String orderCode;
	@NotNull
	private String orderReturnReason;
	@NotNull
	private String returnCategory;
}
