package com.nhnacademy.back.event.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCancelPointEvent {
	private final Long customerId;
	private final Long pointFigure;
}
