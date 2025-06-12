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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.member.domain.dto.request.RequestRegisterMemberDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseRegisterMemberDTO;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.batch.service.RabbitService;
import com.nhnacademy.back.common.config.RabbitConfig;

@WebMvcTest(controllers = MemberRegisterController.class)
class MemberRegisterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MemberService memberService;

	@MockitoBean
	private RabbitService rabbitService;

	@Test
	@DisplayName("회원가입을 통한 회원 생성 테스트")
	void createRegisterTest() throws Exception {

		// Given
		RequestRegisterMemberDTO requestRegisterMemberDTO = RequestRegisterMemberDTO.builder()
			.memberId("user")
			.customerName("김도윤")
			.customerPassword("1234")
			.customerPasswordCheck("1234")
			.customerEmail("user@example.com")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.build();

		ResponseRegisterMemberDTO responseRegisterMemberDTO = ResponseRegisterMemberDTO.builder()
			.memberId(requestRegisterMemberDTO.getMemberId())
			.customerName(requestRegisterMemberDTO.getCustomerName())
			.customerPassword(requestRegisterMemberDTO.getCustomerPassword())
			.customerEmail(requestRegisterMemberDTO.getCustomerEmail())
			.memberBirth(LocalDate.now())
			.memberPhone(requestRegisterMemberDTO.getMemberPhone())
			.build();

		// When
		when(memberService.registerMember(any(RequestRegisterMemberDTO.class)))
			.thenReturn(responseRegisterMemberDTO);
		doNothing().when(rabbitService).sendToRabbitMQ(RabbitConfig.WELCOME_EXCHANGE,
			RabbitConfig.WELCOME_ROUTING_KEY, "user");

		// Then
		mockMvc.perform(post("/api/members/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestRegisterMemberDTO)))
			.andExpect(status().isCreated());

	}

	@Test
	@DisplayName("회원가입을 통한 회원 생성 ValidationFailedException 테스트")
	void createRegisterValidationFailedExceptionTest() throws Exception {

		// Given
		RequestRegisterMemberDTO requestRegisterMemberDTO = RequestRegisterMemberDTO.builder()
			.memberId("user")
			.customerName("김도윤")
			.customerPassword("1234")
			.customerPasswordCheck("1234")
			.build();

		ResponseRegisterMemberDTO responseRegisterMemberDTO = ResponseRegisterMemberDTO.builder()
			.memberId(requestRegisterMemberDTO.getMemberId())
			.customerName(requestRegisterMemberDTO.getCustomerName())
			.customerPassword(requestRegisterMemberDTO.getCustomerPassword())
			.customerEmail(requestRegisterMemberDTO.getCustomerEmail())
			.memberBirth(LocalDate.now())
			.memberPhone(requestRegisterMemberDTO.getMemberPhone())
			.build();

		// When
		when(memberService.registerMember(any(RequestRegisterMemberDTO.class)))
			.thenReturn(responseRegisterMemberDTO);
		doNothing().when(rabbitService).sendToRabbitMQ(RabbitConfig.WELCOME_EXCHANGE,
			RabbitConfig.WELCOME_ROUTING_KEY, "user");

		// Then
		mockMvc.perform(post("/api/members/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestRegisterMemberDTO)))
			.andExpect(status().is4xxClientError());

	}

	@Test
	@DisplayName("회원가입 시 이메일 중복 체크 테스트")
	void checkMemberIdDevTest() throws Exception {

		// Given

		// When
		when(memberService.existsMemberByMemberId("user")).thenReturn(true);

		// Then
		mockMvc.perform(get("/api/members/user/register"))
			.andExpect(status().isOk());

	}

}