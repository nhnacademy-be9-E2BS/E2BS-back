package com.nhnacademy.back.jwt.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenStatus {
	AUTHENTICATED,
	EXPIRED,
	INVALID
}
