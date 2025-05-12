package com.nhnacademy.back.coupon.coupon.exception;

public class CouponAlreadyExistsException extends RuntimeException {
	public CouponAlreadyExistsException(String message) {
		super(message);
	}
}
