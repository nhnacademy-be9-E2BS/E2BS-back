package com.nhnacademy.back.common.error.dto;

import java.time.LocalDateTime;

public record GlobalErrorResponse(String title, int status, LocalDateTime timeStamp) {
}
