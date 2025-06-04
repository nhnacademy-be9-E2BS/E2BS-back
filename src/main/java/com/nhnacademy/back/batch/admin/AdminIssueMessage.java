package com.nhnacademy.back.batch.admin;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminIssueMessage {
	private Long memberId;
	private Long couponId;
	private LocalDateTime expireAt;
}

