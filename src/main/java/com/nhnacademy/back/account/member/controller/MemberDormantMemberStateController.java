package com.nhnacademy.back.account.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberEmailDTO;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.exception.NotFoundMemberStateException;
import com.nhnacademy.back.account.member.exception.UpdateMemberStateFailedException;
import com.nhnacademy.back.account.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "휴면 회원 상태 변경 API", description = "휴면 회원 상태 변경 기능 제공")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dormant")
public class MemberDormantMemberStateController {

	private final MemberService memberService;

	@Operation(summary = "휴먼 회원 Active 상태 변경", description = "휴면 회원 Active 상태 변경 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "휴면 회원 Active 상태 변경 성공"),
			@ApiResponse(responseCode = "500", description = "휴면 회원 상태 조회 실패", content = @Content(schema = @Schema(implementation = NotFoundMemberStateException.class))),
			@ApiResponse(responseCode = "500", description = "휴면 회원 상태 변경 실패", content = @Content(schema = @Schema(implementation = UpdateMemberStateFailedException.class)))
		})
	@PostMapping("/members/{member-id}/dooray")
	public ResponseEntity<Void> changeMemberStateActive(@PathVariable("member-id") String memberId) {
		memberService.changeDormantMemberStateActive(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 회원의 이메일 주소를 응답하는 Controller
	 */
	@Operation(summary = "휴면 회원 Email 조회", description = "휴면 회원 Email 조회 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "휴면 회원 Email 주소 조회 성공"),
			@ApiResponse(responseCode = "500", description = "회원 정보 조회 실패", content = @Content(schema = @Schema(implementation = NotFoundMemberException.class)))
		})
	@GetMapping("/members/{member-id}")
	public ResponseEntity<ResponseMemberEmailDTO> getMemberEmail(@PathVariable("member-id") String memberId) {
		ResponseMemberEmailDTO responseMemberEmailDTO = memberService.getMemberEmail(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberEmailDTO);
	}

}
