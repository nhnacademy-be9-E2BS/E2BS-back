package com.nhnacademy.back.order.order.exception;

public class PaymentApproveFailedException extends RuntimeException {
	public PaymentApproveFailedException(String message) {
		super(message);
	}
}
