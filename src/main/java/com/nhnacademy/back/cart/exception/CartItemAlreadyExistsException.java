package com.nhnacademy.back.cart.exception;

public class CartItemAlreadyExistsException extends RuntimeException {
    public CartItemAlreadyExistsException() {
        super("해당 항목이 장바구니에 이미 존재합니다.");
    }
}
