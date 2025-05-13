package com.nhnacademy.back.coupon.coupon.exception;

public class CouponNotFoundException extends RuntimeException {
	public CouponNotFoundException(String message) {
		super(message);
	}
}
