package com.nhnacademy.back.account.customer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerLoginDTO;
import com.nhnacademy.back.account.customer.domain.dto.response.ResponseCustomerLoginDTO;
import com.nhnacademy.back.account.customer.service.CustomerService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer/login")
public class CustomerLoginController {

	private final CustomerService customerService;

	@PostMapping
	public ResponseEntity<ResponseCustomerLoginDTO> customerLogin(
		@Validated @RequestBody RequestCustomerLoginDTO requestCustomerLoginDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		ResponseCustomerLoginDTO responseCustomerLoginDTO = customerService.postCustomerLogin(requestCustomerLoginDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseCustomerLoginDTO);
	}

}
