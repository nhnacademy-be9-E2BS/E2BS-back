package com.nhnacademy.back.account.address.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

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
import com.nhnacademy.back.account.address.domain.dto.response.ResponseMemberAddressDTO;
import com.nhnacademy.back.account.address.service.AddressService;

@ActiveProfiles("dev")
@WebMvcTest(controllers = AddressController.class)
class AddressControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AddressService addressService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("회원의 배송지 목록 조회 테스트")
	void getMemberAddressesTest() throws Exception {

		// Given
		ResponseMemberAddressDTO responseMemberAddressDTO = ResponseMemberAddressDTO.builder()
			.addressId(1L)
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로 42")
			.addressDetail("우성아파트 7동")
			.addressExtra("공동현관 비밀번호: 1234#")
			.addressAlias("집")
			.addressDefault(true)
			.addressCreatedAt(LocalDateTime.now())
			.addressReceiver("김도윤")
			.addressReceiverPhone("010-1234-5678")
			.build();

		// When
		when(addressService.getMemberAddresses("user")).thenReturn(List.of(
			responseMemberAddressDTO
		));

		// Then
		mockMvc.perform(get("/api/auth/mypage/user/addresses"))
			.andExpect(status().isCreated());

	}

	@Test
	@DisplayName("회원의 특정 배송지 조회 테스트")
	void getAddressTest() throws Exception {

		// Given
		ResponseMemberAddressDTO responseMemberAddressDTO = ResponseMemberAddressDTO.builder()
			.addressId(1L)
			.addressCode("12345")
			.addressInfo("서울특별시 송파구 올림픽로 42")
			.addressDetail("우성아파트 7동")
			.addressExtra("공동현관 비밀번호: 1234#")
			.addressAlias("집")
			.addressDefault(true)
			.addressCreatedAt(LocalDateTime.now())
			.addressReceiver("김도윤")
			.addressReceiverPhone("010-1234-5678")
			.build();

		// When
		when(addressService.getAddressByAddressId("user", 1L)).thenReturn(responseMemberAddressDTO);

		// Then
		mockMvc.perform(get("/api/auth/mypage/user/addresses/1"))
			.andExpect(status().isCreated());

	}

	@Test
	@DisplayName("회원의 특정 배송지 저장 테스트")
	void updateAddressTest() throws Exception {

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
		doNothing().when(addressService).updateAddress(requestMemberAddressSaveDTO, "user", 1L);

		// Then
		mockMvc.perform(put("/api/auth/mypage/user/addresses/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestMemberAddressSaveDTO)))
			.andExpect(status().isCreated());

	}

	@Test
	@DisplayName("회원의 특정 배송지 삭제 테스트")
	void deleteAddressTest() throws Exception {

		// Given

		// When
		doNothing().when(addressService).deleteAddress("user", 1L);

		// Then
		mockMvc.perform(delete("/api/auth/mypage/user/addresses/1"))
			.andExpect(status().isCreated());

	}

	@Test
	@DisplayName("회원의 기본 배송지 설정 테스트")
	void setDefaultAddressTest() throws Exception {

		// Given

		// When
		doNothing().when(addressService).setDefaultAddress("user", 1L);

		// Then
		mockMvc.perform(post("/api/auth/mypage/user/addresses/1/default"))
			.andExpect(status().isCreated());

	}

}