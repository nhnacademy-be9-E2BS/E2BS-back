package com.nhnacademy.back.account.customer.exception;

public class CustomerPasswordNotMatchException extends RuntimeException {
	public CustomerPasswordNotMatchException(String message) {
		super(message);
	}
}
