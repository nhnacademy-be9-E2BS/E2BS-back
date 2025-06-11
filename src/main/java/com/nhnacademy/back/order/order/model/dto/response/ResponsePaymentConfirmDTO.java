package com.nhnacademy.back.order.order.model.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePaymentConfirmDTO {
	private String paymentKey;
	private String orderId;
	private long totalAmount;
	private String provider; // TOSS, NAVER, PAYCO 등
	private LocalDateTime requestedAt;
	private LocalDateTime approvedAt;
}
