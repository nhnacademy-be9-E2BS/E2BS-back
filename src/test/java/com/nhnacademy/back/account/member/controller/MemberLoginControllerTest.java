package com.nhnacademy.back.account.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.back.account.member.service.impl.MemberServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
class MemberLoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private MemberServiceImpl memberService;

	@Test
	@DisplayName("RequestRegisterMemberDTO ValidationFailedException 테스트")
	void requestRegisterMemberDTOValidationFailedExceptionTest() throws Exception {

		// Given
		String requestJson = """
			    {
			        "memberId": 
			    }
			""";

		// When

		// Then
		mockMvc.perform(post("/api/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());

	}

	@Test
	@DisplayName("로그인 성공 시 201 응답")
	void successLoginReturnCREATEDTest() throws Exception {

		// Given

		// When

		// Then

	}

}