package com.nhnacademy.back.common.exception;

public class FileStreamOpenException extends RuntimeException {
	public FileStreamOpenException() {
		super("파일 스트림 열기 실패");
	}
}
