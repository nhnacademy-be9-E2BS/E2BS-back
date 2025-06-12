package com.nhnacademy.back.account.address.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.address.domain.dto.request.RequestMemberAddressSaveDTO;
import com.nhnacademy.back.account.address.service.AddressService;

@ActiveProfiles("dev")
@WebMvcTest(controllers = AddressSaveController.class)
class AddressSaveControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AddressService addressService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("회원의 신규 배송지를 저장 테스트")
	void saveMemberAddressTest() throws Exception {

		// Given
		RequestMemberAddressSaveDTO requestMemberAddressSaveDTO = RequestMemberAddressSaveDTO.builder()
			.addressAlias("집")
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로")
			.addressDetail("우성아파트 7동")
			.addressExtra("공동현관 비밀번호: 1234")
			.addressDefault(true)
			.addressReceiver("김도윤")
			.addressReceiverPhone("010-1234-5678")
			.build();

		// When
		doNothing().when(addressService).saveMemberAddress("user", requestMemberAddressSaveDTO);

		// Then
		mockMvc.perform(post("/api/auth/mypage/user/addresses/form")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestMemberAddressSaveDTO)))
			.andExpect(status().isCreated());

	}

	@Test
	@DisplayName("회원의 신규 배송지를 저장 ValidationFailedException 테스트")
	void saveMemberAddressValidationFailedExceptionTest() throws Exception {

		// Given
		RequestMemberAddressSaveDTO requestMemberAddressSaveDTO = RequestMemberAddressSaveDTO.builder()
			.addressAlias("집")
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로")
			.addressDefault(true)
			.addressReceiver("김도윤")
			.build();

		// When
		doNothing().when(addressService).saveMemberAddress("user", requestMemberAddressSaveDTO);

		// Then
		mockMvc.perform(post("/api/auth/mypage/user/addresses/form")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestMemberAddressSaveDTO)))
			.andExpect(status().is4xxClientError());

	}

}