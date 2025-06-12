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
import com.nhnacademy.back.account.member.exception.AlreadyExistsMemberIdException;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.batch.service.RabbitService;
import com.nhnacademy.back.common.config.RabbitConfig;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원가입 API", description = "회원 회원가입 기능")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberRegisterController {

	private final MemberService memberService;
	private final RabbitService rabbitService;

	/**
	 * 회원가입 시 입력한 아이디와 같은 값이 존재하는지 확인하고 없으면 데이터베이스에 저장
	 */
	@Operation(summary = "회원가입을 통한 회원 생성", description = "회원가입을 통한 회원 생성 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원가입 성공"),
			@ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class))),
			@ApiResponse(responseCode = "500", description = "중복된 아이디", content = @Content(schema = @Schema(implementation = AlreadyExistsMemberIdException.class)))
		})
	@PostMapping("/register")
	public ResponseEntity<ResponseRegisterMemberDTO> createRegister(@Validated
		@Parameter(description = "회원가입 DTO", required = true, schema = @Schema(implementation = RequestRegisterMemberDTO.class))
		@RequestBody RequestRegisterMemberDTO requestRegisterMemberDTO,
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
	@Operation(summary = "회원가입 시 아이디 중복 체크", description = "회원가입 시 아이디 중복 체크 기능")
	@GetMapping("/{member-id}/register")
	public ResponseEntity<Map<String, Boolean>> checkMemberIdDev(@PathVariable("member-id") String memberId) {
		boolean idDuplicateCheck = memberService.existsMemberByMemberId(memberId);
		Map<String, Boolean> response = Collections.singletonMap("available", !idDuplicateCheck);

		return ResponseEntity.ok(response);
	}

}
