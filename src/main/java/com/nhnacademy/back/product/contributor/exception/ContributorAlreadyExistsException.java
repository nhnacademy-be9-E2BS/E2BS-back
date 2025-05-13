package com.nhnacademy.back.product.contributor.exception;

public class ContributorAlreadyExistsException extends RuntimeException {
	public ContributorAlreadyExistsException(String message) {
		super(message);
	}
}
