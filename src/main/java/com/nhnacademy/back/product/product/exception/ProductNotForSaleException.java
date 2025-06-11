package com.nhnacademy.back.product.product.exception;

public class ProductNotForSaleException extends RuntimeException {
	public ProductNotForSaleException(String message) {
		super(message);
	}
}
