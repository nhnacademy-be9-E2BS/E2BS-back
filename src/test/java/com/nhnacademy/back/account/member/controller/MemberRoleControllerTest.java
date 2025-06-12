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
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRoleName;

@WebMvcTest(controllers = MemberRoleController.class)
class MemberRoleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MemberService memberService;

	@Test
	@DisplayName("회원 역할 정보 조회 테스트")
	void getMemberRoleTest() throws Exception {

		// Given
		when(memberService.getMemberRole("user")).thenReturn(MemberRoleName.MEMBER.name());

		// When & Then
		mockMvc.perform(get("/api/members/user/memberrole"))
			.andExpect(status().isOk());

	}

}