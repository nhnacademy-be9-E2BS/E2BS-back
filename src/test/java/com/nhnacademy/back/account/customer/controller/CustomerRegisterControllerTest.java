package com.nhnacademy.back.account.customer.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerRegisterDTO;
import com.nhnacademy.back.account.customer.domain.dto.response.ResponseCustomerDTO;
import com.nhnacademy.back.account.customer.service.CustomerService;

@WebMvcTest(controllers = CustomerRegisterController.class)
class CustomerRegisterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CustomerService customerService;

	@Test
	@DisplayName("비회원 등록 테스트")
	void registerCustomer() throws Exception {
		// given
		RequestCustomerRegisterDTO requestDTO = new RequestCustomerRegisterDTO(
			"guest@example.com", "홍길동", "password123", "password123");

		ResponseCustomerDTO responseDTO = new ResponseCustomerDTO("홍길동", 1L);

		when(customerService.postCustomerRegister(any(RequestCustomerRegisterDTO.class)))
			.thenReturn(responseDTO);

		// when & then
		mockMvc.perform(post("/api/customers/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.customerId").value(1L))
			.andExpect(jsonPath("$.customerName").value("홍길동"));
	}

	@Test
	@DisplayName("비회원 등록 테스트 - 실패(유효성 검증)")
	void registerCustomer_Fail_Validation() throws Exception {
		// given
		RequestCustomerRegisterDTO invalidDTO = new RequestCustomerRegisterDTO(
			"invalid-email", "", "", "");

		// when & then
		mockMvc.perform(post("/api/customers/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDTO)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("이메일 중복 확인 테스트 - 중복 아님")
	void checkCustomerEmail_NotDuplicate() throws Exception {
		// given
		String email = "newuser@example.com";
		when(customerService.isExistsCustomerEmail(email)).thenReturn(false);

		// when & then
		mockMvc.perform(get("/api/customers/register/{customerEmail}", email))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.available").value(true));
	}

	@Test
	@DisplayName("이메일 중복 확인 - 중복")
	void checkCustomerEmail_Duplicate() throws Exception {
		// given
		String email = "existing@example.com";
		when(customerService.isExistsCustomerEmail(email)).thenReturn(true);

		// when & then
		mockMvc.perform(get("/api/customers/register/{customerEmail}", email))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.available").value(false));
	}

}