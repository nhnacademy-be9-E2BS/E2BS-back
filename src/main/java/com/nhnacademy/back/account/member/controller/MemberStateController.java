package com.nhnacademy.back.account.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberStateDTO;
import com.nhnacademy.back.account.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원 상태 조회 API", description = "회원 정보 조회 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{member-id}/memberstate")
public class MemberStateController {

	private final MemberService memberService;

	@Operation(summary = "회원 상태 정보 조회", description = "회원 상태 정보 조회 기능")
	@GetMapping
	public ResponseEntity<ResponseMemberStateDTO> getMemberState(@PathVariable("member-id") String memberId) {
		ResponseMemberStateDTO responseMemberStateDTO = memberService.getMemberState(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberStateDTO);
	}

}
