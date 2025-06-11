package com.nhnacademy.back.account.oauth.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.oauth.model.dto.request.RequestOAuthRegisterDTO;
import com.nhnacademy.back.account.oauth.service.OAuthService;

@WebMvcTest(controllers = OAuthRegisterController.class)
class OAuthRegisterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private OAuthService oAuthService;

	@Test
	@DisplayName("PAYCO 회원가입 기능 테스트")
	void registerOAuthTest() throws Exception {

		// Given
		RequestOAuthRegisterDTO requestOAuthRegisterDTO = RequestOAuthRegisterDTO.builder()
			.memberId("user")
			.email("user@naver.com")
			.mobile("010-1234-5678")
			.name("user")
			.birthdayMMdd("2000-01-01")
			.build();

		// When
		doNothing().when(oAuthService).registerOAuth(requestOAuthRegisterDTO);

		// Then
		mockMvc.perform(post("/api/oauth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestOAuthRegisterDTO)))
			.andExpect(status().isCreated());

	}

	@Test
	@DisplayName("PAYCO 회원가입 기능 ValidationFailedException 테스트")
	void registerOAuthValidationFailedExceptionTest() throws Exception {

		// Given
		RequestOAuthRegisterDTO requestOAuthRegisterDTO = RequestOAuthRegisterDTO.builder()
			.memberId("user")
			.email("user@naver.com")
			.build();

		// When
		doNothing().when(oAuthService).registerOAuth(requestOAuthRegisterDTO);

		// Then
		mockMvc.perform(post("/api/oauth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestOAuthRegisterDTO)))
			.andExpect(status().is4xxClientError());

	}

}