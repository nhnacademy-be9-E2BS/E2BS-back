package com.nhnacademy.back.cart.exception;

public class CartAlreadyExistsException extends RuntimeException {
    public CartAlreadyExistsException() {
        super("해당 회원의 장바구니가 이미 존재합니다.");
    }
}
