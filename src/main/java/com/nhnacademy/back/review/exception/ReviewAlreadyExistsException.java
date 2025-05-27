package com.nhnacademy.back.review.exception;

public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException() {
        super("리뷰가 이미 존재합니다.");
    }
}
