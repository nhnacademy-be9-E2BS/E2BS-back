package com.nhnacademy.back.order.order.exception;

public class OrderDetailNotFoundException extends RuntimeException {
    public OrderDetailNotFoundException() {
        super("주문상세를 찾을 수 없습니다.");
    }
}
