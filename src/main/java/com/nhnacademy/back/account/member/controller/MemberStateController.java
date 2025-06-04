package com.nhnacademy.back.account.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberStateDTO;
import com.nhnacademy.back.account.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/memberstate")
public class MemberStateController {

	private final MemberService memberService;

	@GetMapping
	public ResponseEntity<ResponseMemberStateDTO> getMemberState(@PathVariable("memberId") String memberId) {
		ResponseMemberStateDTO responseMemberStateDTO = memberService.getMemberState(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberStateDTO);
	}
	
}
