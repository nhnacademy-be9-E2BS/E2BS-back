package com.nhnacademy.back.account.memberrank.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.memberrank.domain.dto.response.ResponseMemberRankDTO;
import com.nhnacademy.back.account.memberrank.domain.entity.RankName;
import com.nhnacademy.back.account.memberrank.service.MemberRankService;

@ActiveProfiles("dev")
@WebMvcTest(controllers = MemberRankController.class)
class MemberRankControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MemberRankService memberRankService;

	@Test
	@DisplayName("회원 등급 조회 테스트")
	void getMemberRankServiceTest() throws Exception {

		// Given
		ResponseMemberRankDTO responseMemberRankDTO = new ResponseMemberRankDTO(
			RankName.NORMAL, 1, 1L
		);

		// When
		when(memberRankService.getMemberRanks()).thenReturn(List.of(responseMemberRankDTO));

		// Then
		mockMvc.perform(get("/api/auth/mypage/user/rank"))
			.andExpect(status().isOk());

	}

}