package com.nhnacademy.back.coupon.couponpolicy.exception;

public class CouponPolicyAlreadyExistException extends RuntimeException {
	public CouponPolicyAlreadyExistException(String message) {
		super(message);
	}
}
