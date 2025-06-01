package com.nhnacademy.back.order.order.exception;

public class OrderNotFoundException extends RuntimeException {
	public OrderNotFoundException() {
		super("존재하지않는 주문입니다.");
	}
}
