package com.nhnacademy.back.product.like.exception;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException() {
        super("해당 상품의 좋아요를 찾을 수 없습니다.");
    }
}
