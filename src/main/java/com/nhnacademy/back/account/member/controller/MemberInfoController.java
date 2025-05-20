package com.nhnacademy.back.account.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.member.domain.dto.request.RequestMemberIdDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberInfoDTO;
import com.nhnacademy.back.account.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberInfoController {

	private final MemberService memberService;

	@GetMapping("/{memberId}")
	public ResponseEntity<ResponseMemberInfoDTO> getMember(
		@PathVariable("memberId") String memberId
	) {
		ResponseMemberInfoDTO responseMemberInfoDTO = memberService.getMemberInfo(new RequestMemberIdDTO(memberId));

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberInfoDTO);
	}

}
