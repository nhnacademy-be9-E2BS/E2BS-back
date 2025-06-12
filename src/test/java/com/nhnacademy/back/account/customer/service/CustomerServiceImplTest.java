package com.nhnacademy.back.account.customer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerLoginDTO;
import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerRegisterDTO;
import com.nhnacademy.back.account.customer.domain.dto.response.ResponseCustomerDTO;
import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerEmailAlreadyExistsException;
import com.nhnacademy.back.account.customer.exception.CustomerEmailNotExistsException;
import com.nhnacademy.back.account.customer.exception.CustomerPasswordNotMatchException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.customer.service.impl.CustomerServiceImpl;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

	@Mock
	private CustomerJpaRepository customerJpaRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private CustomerServiceImpl customerService;


	private final String email = "test@example.com";
	private final String password = "password123";
	private final String encodedPassword = "encodedPwd";
	private final String name = "홍길동";

	@Test
	@DisplayName("비회원 가입 테스트")
	void postCustomerLogin() {
		// given
		RequestCustomerLoginDTO dto = new RequestCustomerLoginDTO(email, password);
		Customer customer = Customer.builder()
			.customerEmail(email)
			.customerPassword(encodedPassword)
			.customerName(name)
			.customerId(1L)
			.build();

		when(customerJpaRepository.existsCustomerByCustomerEmail(email)).thenReturn(true);
		when(customerJpaRepository.getCustomerByCustomerEmail(email)).thenReturn(customer);
		when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

		// when
		ResponseCustomerDTO response = customerService.postCustomerLogin(dto);

		// then
		assertEquals(name, response.getCustomerName());
		assertEquals(1L, response.getCustomerId());
	}

	@Test
	@DisplayName("비회원 가입 테스트 - 실패(이메일 존재하지 않음)")
	void postCustomerLogin_Fail_emailNotExists() {
		// given
		RequestCustomerLoginDTO dto = new RequestCustomerLoginDTO(email, password);
		when(customerJpaRepository.existsCustomerByCustomerEmail(email)).thenReturn(false);

		// when & then
		assertThrows(CustomerEmailNotExistsException.class, () -> customerService.postCustomerLogin(dto));
	}

	@Test
	@DisplayName("비회원 가입 테스트 - 실패(비밀번호 불일치)")
	void postCustomerLogin_Fail_passwordMismatch() {
		// given
		Customer customer = Customer.builder()
			.customerEmail(email)
			.customerPassword(encodedPassword)
			.build();
		RequestCustomerLoginDTO dto = new RequestCustomerLoginDTO(email, password);

		when(customerJpaRepository.existsCustomerByCustomerEmail(email)).thenReturn(true);
		when(customerJpaRepository.getCustomerByCustomerEmail(email)).thenReturn(customer);
		when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

		// when & then
		assertThrows(CustomerPasswordNotMatchException.class, () -> customerService.postCustomerLogin(dto));
	}

	@Test
	@DisplayName("비회원 가입 테스트")
	void postCustomerRegister() {
		// given
		RequestCustomerRegisterDTO dto = new RequestCustomerRegisterDTO(email, name, password, password);
		when(customerJpaRepository.existsCustomerByCustomerEmail(email)).thenReturn(false);

		Customer customer = Customer.createCustomerEntity(dto);
		when(customerJpaRepository.save(any(Customer.class))).thenReturn(customer);

		// when
		ResponseCustomerDTO response = customerService.postCustomerRegister(dto);

		// then
		assertEquals(name, response.getCustomerName());
		assertNotNull(response.getCustomerId());
	}

	@Test
	@DisplayName("비회원 가입 테스트 - 실패(이메일 중복)")
	void postCustomerRegister_Fail_emailExists() {
		// given
		RequestCustomerRegisterDTO dto = new RequestCustomerRegisterDTO(email, name, password, password);
		when(customerJpaRepository.existsCustomerByCustomerEmail(email)).thenReturn(true);

		// when & then
		assertThrows(CustomerEmailAlreadyExistsException.class, () -> customerService.postCustomerRegister(dto));
	}

	@Test
	@DisplayName("중복 아이디 검증 메소드 테스트 - true")
	void isExistsCustomerEmail_True() {
		// given
		when(customerJpaRepository.existsCustomerByCustomerEmail(email)).thenReturn(true);

		// when & then
		assertTrue(customerService.isExistsCustomerEmail(email));
	}

	@Test
	@DisplayName("중복 아이디 검증 메소드 테스트 - false")
	void isExistsCustomerEmail_False() {
		// given
		when(customerJpaRepository.existsCustomerByCustomerEmail(email)).thenReturn(false);

		// when & then
		assertFalse(customerService.isExistsCustomerEmail(email));
	}

}