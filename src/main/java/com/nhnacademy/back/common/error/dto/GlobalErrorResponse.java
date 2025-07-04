package com.nhnacademy.back.common.error.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class GlobalErrorResponse {
	private String title;
	private int status;
	private LocalDateTime timeStamp;


	public static ResponseEntity<GlobalErrorResponse> buildErrorResponse(Exception ex, HttpStatus status) {
		GlobalErrorResponse body = GlobalErrorResponse.builder()
			.title(ex.getMessage())
			.status(status.value())
			.timeStamp(LocalDateTime.now())
			.build();
		return ResponseEntity.status(status).body(body);
	}
}