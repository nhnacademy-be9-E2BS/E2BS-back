package com.nhnacademy.back.account.member.exception;

public class AlreadyExistsMemberIdException extends RuntimeException {
  public AlreadyExistsMemberIdException(String message) {
    super(message);
  }
}
