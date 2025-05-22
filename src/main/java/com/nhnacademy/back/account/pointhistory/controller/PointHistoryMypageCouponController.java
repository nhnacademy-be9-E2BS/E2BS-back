package com.nhnacademy.back.account.pointhistory.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponseMemberPointDTO;
import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/{memberId}/points")
public class PointHistoryMypageCouponController {

	private final PointHistoryService pointHistoryService;

	@GetMapping
	public ResponseEntity<ResponseMemberPointDTO> getPoints(
		@PathVariable("memberId") String memberId) {
		ResponseMemberPointDTO responseMemberPointDTO = pointHistoryService.getMemberPoints(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberPointDTO);
	}

}
