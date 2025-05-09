package com.nhnacademy.back.account.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.member.domain.dto.request.RequestRegisterMemberDTO;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

@RestController
@RequestMapping("/api/register")
public class MemberRegisterController {

	@Autowired
	private MemberService memberService;

	/**
	 * 회원가입 시 입력한 아이디와 같은 값이 존재하는지 확인하고 없으면 데이터베이스에 저장
	 */
	@PostMapping
	public ResponseEntity<?> createRegister(
		@Validated @RequestBody RequestRegisterMemberDTO requestRegisterMemberDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		memberService.registerMember(requestRegisterMemberDTO);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
