package com.nhnacademy.back.account.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.member.domain.dto.request.RequestMemberIdDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestMemberInfoDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberInfoDTO;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/members")
public class MemberInfoController {

	private final MemberService memberService;

	@Member
	@GetMapping("/{memberId}")
	public ResponseEntity<ResponseMemberInfoDTO> getMember(@PathVariable("memberId") String memberId) {
		ResponseMemberInfoDTO responseMemberInfoDTO = memberService.getMemberInfo(new RequestMemberIdDTO(memberId));

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberInfoDTO);
	}

	@Member
	@PutMapping("/{memberId}/info")
	public ResponseEntity<Void> updateMemberInfo(@PathVariable("memberId") String memberId,
		@Validated @RequestBody RequestMemberInfoDTO requestMemberInfoDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		memberService.updateMemberInfo(requestMemberInfoDTO);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Member
	@PostMapping("/{memberId}/info")
	public ResponseEntity<Void> withdrawMember(@PathVariable("memberId") String memberId) {
		memberService.withdrawMember(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
