package com.nhnacademy.back.common.exception;

public class MinioUploadException extends RuntimeException {
	public MinioUploadException() {
		super("MinIO 업로드 실패");
	}
}
