package com.nhnacademy.back.account.member.exception;

public class LoginMemberIsNotExistsException extends RuntimeException {
	public LoginMemberIsNotExistsException(String message) {
		super(message);
	}
}
