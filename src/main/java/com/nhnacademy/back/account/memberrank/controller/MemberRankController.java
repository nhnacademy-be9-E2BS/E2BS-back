package com.nhnacademy.back.account.memberrank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.memberrank.domain.dto.response.ResponseMemberRankDTO;
import com.nhnacademy.back.account.memberrank.service.MemberRankService;
import com.nhnacademy.back.common.annotation.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원 등급 조회 API", description = "회원 등급 조회 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/mypage/{member-id}/rank")
public class MemberRankController {

	private final MemberRankService memberRankService;

	@Operation(summary = "회원 등급 조회", description = "회원 등급 조회 기능 제공")
	@Member
	@GetMapping
	public ResponseEntity<List<ResponseMemberRankDTO>> getMemberRankService(
		@PathVariable("member-id") String memberId) {
		List<ResponseMemberRankDTO> response = memberRankService.getMemberRanks();

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
