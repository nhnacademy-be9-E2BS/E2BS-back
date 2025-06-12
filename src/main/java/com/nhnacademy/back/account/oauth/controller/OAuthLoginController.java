package com.nhnacademy.back.account.oauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.oauth.model.dto.response.ResponseCheckOAuthIdDTO;
import com.nhnacademy.back.account.oauth.service.OAuthService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "PAYCO 로그인", description = "PAYCO 로그인 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth/login")
public class OAuthLoginController {

	private final OAuthService oAuthService;

	/**
	 * oauth 계정이 이미 회원 가입이 되어 있는지 확인하는 컨트롤러
	 */
	@Operation(summary = "PAYCO 계정 중복체크", description = "PAYCO 계정 중복체크 기능")
	@GetMapping("/{member-id}")
	public ResponseEntity<ResponseCheckOAuthIdDTO> checkOAuthId(@PathVariable("member-id") String memberId) {
		ResponseCheckOAuthIdDTO responseCheckOAuthIdDTO = new ResponseCheckOAuthIdDTO(
			oAuthService.checkOAuthId(memberId)
		);

		return ResponseEntity.status(HttpStatus.OK).body(responseCheckOAuthIdDTO);
	}

	@Operation(summary = "PAYCO 마지막 로그인 시간 저장 기능", description = "PAYCO 마지막 로그인 시간 저장 기능 제공",
		responses = {
			@ApiResponse(responseCode = "200", description = "PAYCO 마지막 로그인 시간 저장 성공 응답"),
			@ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@GetMapping("/members/{member-id}")
	public ResponseEntity<Void> loginOAuth(@PathVariable("member-id") String memberId) {
		oAuthService.paycoLastLogin(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
