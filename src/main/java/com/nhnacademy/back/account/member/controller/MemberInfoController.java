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
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.exception.UpdateMemberInfoFailedException;
import com.nhnacademy.back.account.member.exception.UpdateMemberStateFailedException;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "마이페이지 회원 정보 API", description = "회원 정보 관리 기능 제공")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/members")
public class MemberInfoController {

	private final MemberService memberService;

	@Operation(summary = "회원의 정보 조회", description = "회원 정보 조회 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원의 정보 조회 기능 성공"),
			@ApiResponse(responseCode = "500", description = "회원 정보 조회 실패",
				content = @Content(schema = @Schema(implementation = NotFoundMemberException.class)))
		})
	@Member
	@GetMapping("/{member-id}")
	public ResponseEntity<ResponseMemberInfoDTO> getMember(@PathVariable("member-id") String memberId) {
		ResponseMemberInfoDTO responseMemberInfoDTO = memberService.getMemberInfo(new RequestMemberIdDTO(memberId));

		return ResponseEntity.status(HttpStatus.OK).body(responseMemberInfoDTO);
	}

	@Operation(summary = "회원 정보 변경", description = "회원 정보 변경 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원의 정보 변경 기능 성공"),
			@ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class))),
			@ApiResponse(responseCode = "500", description = "회원 정보 조회 실패", content = @Content(schema = @Schema(implementation = NotFoundMemberException.class))),
			@ApiResponse(responseCode = "500", description = "회원 정보 변경 실패", content = @Content(schema = @Schema(implementation = UpdateMemberInfoFailedException.class)))
		})
	@Member
	@PutMapping("/{member-id}/info")
	public ResponseEntity<Void> updateMemberInfo(@PathVariable("member-id") String memberId,
		@Validated @Parameter(description = "회원 정보 요청 DTO", required = true, schema = @Schema(implementation = RequestMemberInfoDTO.class))
		@RequestBody RequestMemberInfoDTO requestMemberInfoDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		memberService.updateMemberInfo(requestMemberInfoDTO);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Operation(summary = "회원 탈퇴", description = "회원 탈퇴 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원의 정보 변경 기능 성공"),
			@ApiResponse(responseCode = "500", description = "회원 정보 조회 실패", content = @Content(schema = @Schema(implementation = NotFoundMemberException.class))),
			@ApiResponse(responseCode = "500", description = "회원 탈퇴 실패", content = @Content(schema = @Schema(implementation = UpdateMemberStateFailedException.class)))
		})
	@Member
	@PostMapping("/{member-id}/info")
	public ResponseEntity<Void> withdrawMember(@PathVariable("member-id") String memberId) {
		memberService.withdrawMember(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
