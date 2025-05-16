package com.nhnacademy.back.product.category.exception;

public class ProductCategoryCreateNotAllowException extends RuntimeException {
	public ProductCategoryCreateNotAllowException() {
		super("카테고리 설정 불가능 (최대 10개까지 설정 가능)");
	}
}
