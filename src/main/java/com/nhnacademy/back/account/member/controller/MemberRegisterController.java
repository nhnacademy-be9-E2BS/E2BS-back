package com.nhnacademy.back.account.member.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.member.domain.dto.request.RequestRegisterMemberDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseRegisterMemberDTO;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.batch.service.RabbitService;
import com.nhnacademy.back.common.config.RabbitConfig;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/register")
public class MemberRegisterController {

	private final MemberService memberService;
	private final PointHistoryService pointHistoryService;
	private final RabbitService rabbitService;


	/**
	 * 회원가입 시 입력한 아이디와 같은 값이 존재하는지 확인하고 없으면 데이터베이스에 저장
	 */
	@PostMapping
	public ResponseEntity<ResponseRegisterMemberDTO> createRegister(
		@Validated @RequestBody RequestRegisterMemberDTO requestRegisterMemberDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		// 회원가입 (member 테이블에 저장)
		ResponseRegisterMemberDTO responseRegisterMemberDTO = memberService.registerMember(requestRegisterMemberDTO);

		// 웰컴 쿠폰 MQ 발급
		rabbitService.sendToRabbitMQ(
			RabbitConfig.WELCOME_EXCHANGE,
			RabbitConfig.WELCOME_ROUTING_KEY,
			responseRegisterMemberDTO.getMemberId());

		return ResponseEntity.status(HttpStatus.CREATED).body(responseRegisterMemberDTO);
	}

	/**
	 * 회원 가입 아이디 중복체크 메서드
	 */
	@GetMapping("/members/{memberId}")
	public ResponseEntity<Map<String, Boolean>> checkMemberIdDev(@PathVariable("memberId") String memberId) {
		boolean idDuplicateCheck = memberService.existsMemberByMemberId(memberId);
		Map<String, Boolean> response = Collections.singletonMap("available", !idDuplicateCheck);

		return ResponseEntity.ok(response);
	}

}
