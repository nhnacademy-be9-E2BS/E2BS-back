package com.nhnacademy.back.common.exception;

public class MinioDeleteException extends RuntimeException {
	public MinioDeleteException() {
		super("MinIO 삭제 실패");
	}
}
