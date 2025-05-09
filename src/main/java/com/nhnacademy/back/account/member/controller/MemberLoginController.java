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

import com.nhnacademy.back.account.member.domain.dto.MemberDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestLoginMemberDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseLoginMemberDTO;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

@RestController
@RequestMapping("/api/login")
public class MemberLoginController {

	@Autowired
	private MemberService memberService;

	/**
	 * Member 테이블에 사용자가 입력한 ID 값이 존재하는지 확인
	 * ID에 해당하는 회원 정보들을 가져와서 응답
	 */
	@PostMapping
	public ResponseEntity<ResponseLoginMemberDTO> postLogin(
		@Validated @RequestBody RequestLoginMemberDTO requestLoginMemberDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		MemberDTO member = memberService.loginMember(new RequestLoginMemberDTO(requestLoginMemberDTO.getMemberId()));

		ResponseLoginMemberDTO responseLoginMemberDTO = new ResponseLoginMemberDTO(member.getMemberId(),
			member.getCustomer().getCustomerPassword(), member.getMemberRole().getMemberRoleName());

		return ResponseEntity.status(HttpStatus.CREATED).body(responseLoginMemberDTO);
	}

}
