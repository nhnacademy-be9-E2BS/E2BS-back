package com.nhnacademy.back.account.member.exception;

public class NotFoundMemberException extends RuntimeException {
	public NotFoundMemberException(String message) {
		super(message);
	}
}
