package com.nhnacademy.back.account.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nhnacademy.back.account.member.domain.dto.request.RequestRegisterMemberDTO;
import com.nhnacademy.back.account.member.service.impl.MemberServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
class MemberRegisterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberServiceImpl memberService;

	@Test
	@DisplayName("RequestRegisterMemberDTO ValidationFailedException 테스트")
	void requestRegisterMemberDTOValidationFailedExceptionTest() throws Exception {

		// Given
		String requestJson = """
			{
			    "memberId": "nhn1",
			    "customerName": "NHN",
			    "customerPassword": null,
			    "customerPasswordCheck": null,
			    "customerEmail": "nhn@gmail.com",
			    "memberBirth": "2000-01-01",
			    "memberPhone": "01012345678"
			}
			""";

		// When

		// Then
		mockMvc.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());

	}

	@Test
	@DisplayName("회원가입 성공 시 201 응답")
	void successRegisterReturnCREATEDTest() throws Exception {

		// Given
		String requestJson = """
			{
			    "memberId": "nhn1",
			    "customerName": "NHN",
			    "customerPassword": 1234,
			    "customerPasswordCheck": 1234,
			    "customerEmail": "nhn@gmail.com",
			    "memberBirth": "2000-01-01",
			    "memberPhone": "01012345678"
			}
			""";

		String memberId = "nhn1";
		String customerName = "NHN";
		String customerPassword = "1234";
		String customerPasswordCheck = "1234";
		String customerEmail = "nhn@gmail.com";
		LocalDate memberBirth = LocalDate.now();
		String memberPhone = "01012345678";

		RequestRegisterMemberDTO requestRegisterMemberDTO = new RequestRegisterMemberDTO(
			memberId, customerName, customerPassword, customerPasswordCheck,
			customerEmail, memberBirth, memberPhone
		);

		// When
		doNothing().when(memberService).registerMember(any());

		// Then
		mockMvc.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isCreated());

	}

}