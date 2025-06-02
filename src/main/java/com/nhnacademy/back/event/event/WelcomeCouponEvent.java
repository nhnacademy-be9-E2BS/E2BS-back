package com.nhnacademy.back.event.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WelcomeCouponEvent {
	private final String memberId;
}
