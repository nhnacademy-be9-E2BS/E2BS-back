package com.nhnacademy.back.product.tag.exception;

public class TagAlreadyExistsException extends RuntimeException {
	public TagAlreadyExistsException(String message) {
		super(message);
	}
}
