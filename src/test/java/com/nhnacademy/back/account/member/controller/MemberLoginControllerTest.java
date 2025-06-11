package com.nhnacademy.back.account.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.member.domain.dto.MemberDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestLoginMemberDTO;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.domain.entity.RankName;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRoleName;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberStateName;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuthName;

@ActiveProfiles("dev")
@WebMvcTest(controllers = MemberLoginController.class)
class MemberLoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MemberService memberService;

	@Test
	@DisplayName("로그인 기능 테스트")
	void postLoginTest() throws Exception {

		// Given
		MemberDTO memberDTO = MemberDTO.builder()
			.customer(new Customer("user@naver.com", "1234", "user"))
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.memberCreatedAt(LocalDate.now())
			.memberLoginLatest(LocalDate.now())
			.memberRank(new MemberRank(1, RankName.NORMAL, 1, 1))
			.memberState(new MemberState(1, MemberStateName.ACTIVE))
			.memberRole(new MemberRole(1, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1, SocialAuthName.WEB))
			.build();

		RequestLoginMemberDTO requestLoginMemberDTO = new RequestLoginMemberDTO("user");

		// When
		when(memberService.loginMember(any(RequestLoginMemberDTO.class))).thenReturn(memberDTO);

		// Then
		mockMvc.perform(post("/api/members/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestLoginMemberDTO)))
			.andExpect(status().isCreated());

	}

	@Test
	@DisplayName("로그인 기능 ValidationFailedException 테스트")
	void postLoginValidationFailedExceptionTest() throws Exception {

		// Given
		MemberDTO memberDTO = MemberDTO.builder()
			.customer(new Customer("user@naver.com", "1234", "user"))
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.memberCreatedAt(LocalDate.now())
			.memberLoginLatest(LocalDate.now())
			.memberRank(new MemberRank(1, RankName.NORMAL, 1, 1))
			.memberState(new MemberState(1, MemberStateName.ACTIVE))
			.memberRole(new MemberRole(1, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1, SocialAuthName.WEB))
			.build();

		RequestLoginMemberDTO requestLoginMemberDTO = new RequestLoginMemberDTO();

		// When
		when(memberService.loginMember(any(RequestLoginMemberDTO.class))).thenReturn(memberDTO);

		// Then
		mockMvc.perform(post("/api/members/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestLoginMemberDTO)))
			.andExpect(status().is4xxClientError());

	}

}