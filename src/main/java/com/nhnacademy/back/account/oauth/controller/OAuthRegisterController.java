package com.nhnacademy.back.account.oauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.oauth.model.dto.request.RequestOAuthRegisterDTO;
import com.nhnacademy.back.account.oauth.service.OAuthService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/oauth/register")
@RequiredArgsConstructor
public class OAuthRegisterController {

	private final OAuthService oAuthService;

	/**
	 * OAuth 계정 회원 가입
	 */
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
