package com.nhnacademy.back.account.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberEmailDTO;
import com.nhnacademy.back.account.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dormant")
public class MemberDormantMemberStateController {

	private final MemberService memberService;

	@PostMapping("/members/{memberId}/dooray")
	public ResponseEntity<Void> changeMemberStateActive(@PathVariable("memberId") String memberId) {
		memberService.changeDormantMemberStateActive(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 회원의 이메일 주소를 응답하는 Controller
	 */
	@GetMapping("/members/{memberId}")
	public ResponseEntity<ResponseMemberEmailDTO> getMemberEmail(@PathVariable("memberId") String memberId) {
		ResponseMemberEmailDTO responseMemberEmailDTO = memberService.getMemberEmail(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberEmailDTO);
	}

}
