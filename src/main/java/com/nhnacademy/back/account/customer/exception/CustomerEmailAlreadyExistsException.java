package com.nhnacademy.back.account.customer.exception;

public class CustomerEmailAlreadyExistsException extends RuntimeException {
	public CustomerEmailAlreadyExistsException(String message) {
		super(message);
	}
}
