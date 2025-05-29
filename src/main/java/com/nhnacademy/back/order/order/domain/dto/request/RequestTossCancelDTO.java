package com.nhnacademy.back.order.order.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestTossCancelDTO {
	private String cancelReason;
	private long cancelAmount;
}
