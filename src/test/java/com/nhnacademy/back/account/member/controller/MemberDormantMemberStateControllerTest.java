package com.nhnacademy.back.account.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberEmailDTO;
import com.nhnacademy.back.account.member.service.MemberService;

@ActiveProfiles("dev")
@WebMvcTest(controllers = MemberDormantMemberStateController.class)
class MemberDormantMemberStateControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MemberService memberService;

	@Test
	@DisplayName("휴면 회원 상태 변경 테스트")
	void changeMemberStateActiveTest() throws Exception {

		// Given

		// When
		doNothing().when(memberService).changeDormantMemberStateActive("user");

		// Then
		mockMvc.perform(post("/api/dormant/members/user/dooray"))
			.andExpect(status().isCreated());

	}

	@Test
	@DisplayName("휴면 회원 Email 조회 테스트")
	void getMemberEmailTest() throws Exception {

		// Given
		ResponseMemberEmailDTO responseMemberEmailDTO = new ResponseMemberEmailDTO(
			"user@naver.com"
		);

		// When
		when(memberService.getMemberEmail("user")).thenReturn(responseMemberEmailDTO);

		// Then
		mockMvc.perform(get("/api/dormant/members/user"))
			.andExpect(status().isOk());

	}

}