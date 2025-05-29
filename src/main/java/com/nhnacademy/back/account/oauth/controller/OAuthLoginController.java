package com.nhnacademy.back.account.oauth.controller;

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

import com.nhnacademy.back.account.oauth.model.dto.request.RequestOAuthLoginDTO;
import com.nhnacademy.back.account.oauth.model.dto.response.ResponseCheckOAuthIdDTO;
import com.nhnacademy.back.account.oauth.service.OAuthService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth/login")
public class OAuthLoginController {

	private final OAuthService oAuthService;

	/**
	 * oauth 계정이 이미 회원 가입이 되어 있는지 확인하는 컨트롤러
	 */
	@GetMapping("/{memberId}")
	public ResponseEntity<ResponseCheckOAuthIdDTO> checkOAuthId(@PathVariable("memberId") String memberId) {
		ResponseCheckOAuthIdDTO responseCheckOAuthIdDTO = new ResponseCheckOAuthIdDTO(
			oAuthService.checkOAuthId(memberId)
		);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseCheckOAuthIdDTO);
	}

	@PostMapping
	public ResponseEntity<Void> loginOAuth(@Validated @RequestBody RequestOAuthLoginDTO requestOAuthLoginDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
