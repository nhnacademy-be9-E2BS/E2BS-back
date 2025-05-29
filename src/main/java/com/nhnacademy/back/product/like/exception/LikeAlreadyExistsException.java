package com.nhnacademy.back.product.like.exception;

public class LikeAlreadyExistsException extends RuntimeException {
    public LikeAlreadyExistsException() {
        super("해당 상품의 좋아요가 되어있습니다.");
    }
}
