package com.nhnacademy.back.common.exception;

public class InvalidImageFormatException extends RuntimeException {
	public InvalidImageFormatException(String contentType) {
		super("이미지 파일 형식만 업로드 할 수 있습니다. 현재 파일 contentType : " + contentType);
	}
}
