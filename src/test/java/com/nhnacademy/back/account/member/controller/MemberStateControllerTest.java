package com.nhnacademy.back.account.member.controller;

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
import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberStateDTO;
import com.nhnacademy.back.account.member.service.MemberService;

@WebMvcTest(controllers = MemberStateController.class)
class MemberStateControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MemberService memberService;

	@Test
	@DisplayName("회원 상태 정보 조회 테스트")
	void getMemberStateTest() throws Exception {

		// Given
		ResponseMemberStateDTO responseMemberStateDTO = new ResponseMemberStateDTO(
			"ACTIVE"
		);

		// When
		when(memberService.getMemberState("user")).thenReturn(responseMemberStateDTO);

		// Then
		mockMvc.perform(get("/api/members/user/memberstate"))
			.andExpect(status().isOk());

	}

}