package com.nhnacademy.back.cart.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException() {
        super("장바구니 항목을 찾을 수 없습니다.");
    }
}
