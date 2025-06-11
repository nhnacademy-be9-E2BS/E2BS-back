package com.nhnacademy.back.account.oauth.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.oauth.service.OAuthService;

@WebMvcTest(controllers = OAuthLoginController.class)
class OAuthLoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private OAuthService oAuthService;

	@Test
	@DisplayName("OAuth 계정이 이미 있는지 확인 테스트")
	void checkOAuthIdTest() throws Exception {

		// Given

		// When
		when(oAuthService.checkOAuthId("user")).thenReturn(true);

		// Then
		mockMvc.perform(get("/api/oauth/login/user"))
			.andExpect(status().isOk());

	}

}