package com.nhnacademy.back.common.swagger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonResponse<T> {
	private final int code;
	private final String message;
	private final T data;

	// 성공 응답용 메서드
	public static <T> CommonResponse<T> success(T data) {
		return new CommonResponse<>(Result.OK.getCode(), Result.OK.getMessage(), data);
	}

	public static <T> CommonResponse<T> success() {
		return new CommonResponse<>(Result.OK.getCode(), Result.OK.getMessage(), null);
	}

	// 실패 응답용 메서드
	public static <T> CommonResponse<T> error(T data) {
		return new CommonResponse<>(Result.FAIL.getCode(), Result.FAIL.getMessage(), data);
	}

	public static <T> CommonResponse<T> error() {
		return new CommonResponse<>(Result.FAIL.getCode(), Result.FAIL.getMessage(), null);
	}

}
