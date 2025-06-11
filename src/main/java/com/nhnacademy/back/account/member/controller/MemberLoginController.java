package com.nhnacademy.back.account.member.controller;

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
import com.nhnacademy.back.account.member.exception.LoginMemberIsNotExistsException;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "로그인 API", description = "로그인 기능 제공")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members/login")
public class MemberLoginController {

	private final MemberService memberService;

	/**
	 * Member 테이블에 사용자가 입력한 ID 값이 존재하는지 확인
	 * ID에 해당하는 회원 정보들을 가져와서 응답
	 */
	@Operation(summary = "로그인 기능", description = "회원 및 관리자 로그인 기능 제공",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원 및 관리자 로그인 기능 제공"),
			@ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class))),
			@ApiResponse(responseCode = "500", description = "회원 및 관리자 로그인 기능 실패", content = @Content(schema = @Schema(implementation = LoginMemberIsNotExistsException.class)))
		})
	@PostMapping
	public ResponseEntity<ResponseLoginMemberDTO> postLogin(@Validated
		@Parameter(description = "로그인 요청 DTO", required = true, schema = @Schema(implementation = RequestLoginMemberDTO.class))
		@RequestBody RequestLoginMemberDTO requestLoginMemberDTO,
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
