package com.nhnacademy.back.account.pointhistory.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponsePointHistoryDTO;
import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PointHistoryController {

	private final PointHistoryService pointHistoryService;

	@GetMapping("/api/auth/mypage/{memberId}/pointHistory")
	public ResponseEntity<Page<ResponsePointHistoryDTO>> getPointList(@PathVariable("memberId") String memberId, Pageable pageable) {
		Page<ResponsePointHistoryDTO> response = pointHistoryService.getPointHistoryByMemberId(memberId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
