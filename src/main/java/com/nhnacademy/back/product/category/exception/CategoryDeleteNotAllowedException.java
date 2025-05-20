package com.nhnacademy.back.product.category.exception;

public class CategoryDeleteNotAllowedException extends RuntimeException {
	public CategoryDeleteNotAllowedException(String message) {
		super(message);
	}
}
