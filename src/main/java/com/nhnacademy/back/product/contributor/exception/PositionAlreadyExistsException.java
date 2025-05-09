package com.nhnacademy.back.product.contributor.exception;

public class PositionAlreadyExistsException extends RuntimeException{
	public PositionAlreadyExistsException(String message) {
		super(message);
	}
}
