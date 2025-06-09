package com.nhnacademy.back.account.pointhistory.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponseMemberPointDTO;
import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.common.annotation.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/mypage/{memberId}/points")
@Tag(name = "마이페이지 포인트 총합", description = "마이페이지 회원 포인트 총합 조회 API")
public class PointHistoryMypageCouponController {

	private final PointHistoryService pointHistoryService;

	@Operation(
		summary = "회원 포인트 총합 조회",
		description = "해당 회원의 현재 포인트 총합 조회"
	)
	@ApiResponse(responseCode = "201", description = "회원 포인트 조회 성공")
	@Member
	@GetMapping
	public ResponseEntity<ResponseMemberPointDTO> getPoints(
		@Parameter(description = "회원 ID", example = "user")
		@PathVariable("memberId") String memberId) {

		ResponseMemberPointDTO responseMemberPointDTO = pointHistoryService.getMemberPoints(memberId);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberPointDTO);
	}

}
