package com.nhnacademy.back.account.customer.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException() {
        super("이미 고객이 존재합니다.");
    }
}
