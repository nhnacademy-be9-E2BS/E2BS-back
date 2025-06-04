package com.nhnacademy.back.account.customer.exception;

public class CustomerEmailNotExistsException extends RuntimeException {
	public CustomerEmailNotExistsException(String message) {
		super(message);
	}
}
