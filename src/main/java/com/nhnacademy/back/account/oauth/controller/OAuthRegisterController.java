package com.nhnacademy.back.account.oauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.oauth.exception.RegisterOAuthFailedException;
import com.nhnacademy.back.account.oauth.model.dto.request.RequestOAuthRegisterDTO;
import com.nhnacademy.back.account.oauth.service.OAuthService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "PAYCO 회원가입", description = "PAYCO 회원가입 기능")
@RestController
@RequestMapping("/api/oauth/register")
@RequiredArgsConstructor
public class OAuthRegisterController {

	private final OAuthService oAuthService;

	/**
	 * OAuth 계정 회원 가입
	 */
	@Operation(summary = "PAYCO 회원가입 기능", description = "PAYCO 회원가입 기능 제공",
		responses = {
			@ApiResponse(responseCode = "200", description = "PAYCO 회원가입 성공"),
			@ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class))),
			@ApiResponse(responseCode = "500", description = "PAYCO 회원가입 실패", content = @Content(schema = @Schema(implementation = RegisterOAuthFailedException.class)))
		})
	@PostMapping
	public ResponseEntity<Void> registerOAuth(@Validated @RequestBody RequestOAuthRegisterDTO requestOAuthRegisterDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		oAuthService.registerOAuth(requestOAuthRegisterDTO);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
