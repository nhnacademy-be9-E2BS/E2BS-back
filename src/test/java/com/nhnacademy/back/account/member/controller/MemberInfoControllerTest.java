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
import com.nhnacademy.back.account.member.domain.dto.request.RequestMemberIdDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestMemberInfoDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberInfoDTO;
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
@WebMvcTest(controllers = MemberInfoController.class)
class MemberInfoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MemberService memberService;

	@Test
	@DisplayName("회원 정보 조회 테스트")
	void getMemberTest() throws Exception {

		// Given
		ResponseMemberInfoDTO responseMemberInfoDTO = ResponseMemberInfoDTO.builder()
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

		// When
		when(memberService.getMemberInfo(new RequestMemberIdDTO("user"))).thenReturn(responseMemberInfoDTO);

		// Then
		mockMvc.perform(get("/api/auth/members/user"))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("회원 정보 수정 테스트")
	void updateMemberInfoTest() throws Exception {

		// Given
		RequestMemberInfoDTO requestMemberInfoDTO = RequestMemberInfoDTO.builder()
			.memberId("user")
			.customerName("user")
			.customerEmail("user@naver.com")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.customerPassword("1234")
			.customerPasswordCheck("1234")
			.build();

		// When
		doNothing().when(memberService).updateMemberInfo(requestMemberInfoDTO);

		// Then
		mockMvc.perform(put("/api/auth/members/user/info")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestMemberInfoDTO)))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("회원 정보 수정 ValidationFailedException 테스트")
	void updateMemberInfoValidationFailedExceptionTest() throws Exception {

		// Given
		RequestMemberInfoDTO requestMemberInfoDTO = RequestMemberInfoDTO.builder()
			.memberId("user")
			.customerName("user")
			.customerEmail("user@naver.com")
			.customerPassword("1234")
			.customerPasswordCheck("1234")
			.build();

		// When
		doNothing().when(memberService).updateMemberInfo(requestMemberInfoDTO);

		// Then
		mockMvc.perform(put("/api/auth/members/user/info")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestMemberInfoDTO)))
			.andExpect(status().is4xxClientError());

	}

	@Test
	@DisplayName("회원 탈퇴 테스트")
	void withdrawMemberTest() throws Exception {

		// Given

		// When
		doNothing().when(memberService).withdrawMember("user");

		// Then
		mockMvc.perform(post("/api/auth/members/user/info"))
			.andExpect(status().isCreated());

	}

}