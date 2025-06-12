package com.nhnacademy.back.account.customer.controller;

import static org.mockito.ArgumentMatchers.*;
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
import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerLoginDTO;
import com.nhnacademy.back.account.customer.domain.dto.response.ResponseCustomerDTO;
import com.nhnacademy.back.account.customer.service.CustomerService;

@WebMvcTest(controllers = CustomerLoginController.class)
class CustomerLoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CustomerService customerService;


	@Test
	@DisplayName("비회원 로그인 테스트")
	void customerLogin() throws Exception {
		// given
		RequestCustomerLoginDTO loginDTO = new RequestCustomerLoginDTO("test@example.com", "securePassword");
		ResponseCustomerDTO responseDTO = new ResponseCustomerDTO("홍길동", 1L);

		when(customerService.postCustomerLogin(any(RequestCustomerLoginDTO.class)))
			.thenReturn(responseDTO);

		// when & then
		mockMvc.perform(post("/api/customers/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.customerId").value(1L))
			.andExpect(jsonPath("$.customerName").value("홍길동"));
	}

	@Test
	@DisplayName("비회원 로그인 테스트 - 실패(유효성 검증)")
	void customerLogin_Fail_Validation() throws Exception {
		// given
		RequestCustomerLoginDTO invalidDTO = new RequestCustomerLoginDTO(null, "pwd");
		String jsonRequest = objectMapper.writeValueAsString(invalidDTO);

		// when & then
		mockMvc.perform(post("/api/customers/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isBadRequest());
	}
	
}
