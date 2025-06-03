package com.nhnacademy.back.order.order.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestOrderReturnDTO {
	private String orderCode;
	private String orderReturnReason;
	private String returnCategory;
}
