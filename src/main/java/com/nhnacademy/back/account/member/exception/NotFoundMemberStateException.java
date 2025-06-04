package com.nhnacademy.back.account.member.exception;

public class NotFoundMemberStateException extends RuntimeException {
	public NotFoundMemberStateException(String message) {
		super(message);
	}
}
