package com.nhnacademy.back.product.tag.exception;

public class TagNotFoundException extends RuntimeException {
	public TagNotFoundException(String message) {
		super(message);
	}
}
