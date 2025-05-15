package com.nhnacademy.back.product.category.exception;

public class CategoryUpdateNotAllowedException extends RuntimeException {
	public CategoryUpdateNotAllowedException(String message) {
		super(message);
	}
}
