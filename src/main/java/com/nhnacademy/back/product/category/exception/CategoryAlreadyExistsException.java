package com.nhnacademy.back.product.category.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
	public CategoryAlreadyExistsException(String message) {
		super(message);
	}
}
