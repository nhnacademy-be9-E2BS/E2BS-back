package com.nhnacademy.back.account.pointhistory.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponsePointHistoryDTO;
import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.common.annotation.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "포인트 내역", description = "마이페이지 회원의 포인트 내역 조회 API")
public class PointHistoryController {

	private final PointHistoryService pointHistoryService;

	@Operation(
		summary = "회원 포인트 내역 조회",
		description = "회원 ID로 해당 회원의 포인트 적립/사용 내역을 페이징 처리하여 조회"
	)
	@ApiResponse(responseCode = "200", description = "포인트 내역 조회 성공")
	@Member
	@GetMapping("/api/auth/mypage/{memberId}/pointHistory")
	public ResponseEntity<Page<ResponsePointHistoryDTO>> getPointList(
		@Parameter(description = "회원 ID", example = "user") @PathVariable("memberId") String memberId,
		Pageable pageable) {

		Page<ResponsePointHistoryDTO> response = pointHistoryService.getPointHistoryByMemberId(memberId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
