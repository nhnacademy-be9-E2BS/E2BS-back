package com.nhnacademy.back.product.category.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
	public CategoryAlreadyExistsException() {
		super("카테고리명이 이미 존재합니다.");
	}
}
