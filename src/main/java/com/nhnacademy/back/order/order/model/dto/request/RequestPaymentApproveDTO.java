package com.nhnacademy.back.order.order.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestPaymentApproveDTO {
	private String orderId;
	private String paymentKey;
	private long amount;
	private String provider;
}
