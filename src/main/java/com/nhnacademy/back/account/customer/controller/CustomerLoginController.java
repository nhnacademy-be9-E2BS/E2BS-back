package com.nhnacademy.back.account.customer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerLoginDTO;
import com.nhnacademy.back.account.customer.domain.dto.response.ResponseCustomerDTO;
import com.nhnacademy.back.account.customer.service.CustomerService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CustomerLoginController {

	private final CustomerService customerService;

	/**
	 * 비회원 로그인
	 */
	@PostMapping("/api/customers/login")
	public ResponseEntity<ResponseCustomerDTO> customerLogin(@Validated @RequestBody RequestCustomerLoginDTO requestCustomerLoginDTO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		ResponseCustomerDTO customer = customerService.postCustomerLogin(requestCustomerLoginDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(customer);
	}

}
