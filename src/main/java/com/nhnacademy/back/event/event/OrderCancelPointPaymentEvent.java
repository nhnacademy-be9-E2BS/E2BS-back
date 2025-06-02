package com.nhnacademy.back.event.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCancelPointPaymentEvent {
	private final String memberId;
	private final Long pointFigure;
}
