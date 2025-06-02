package com.nhnacademy.back.account.customer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerLoginDTO;
import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerRegisterDTO;
import com.nhnacademy.back.account.customer.service.CustomerService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CustomerRestController {

	private final CustomerService customerService;


	@PostMapping("/api/customers/register")
	public ResponseEntity<Void> registerCustomer(@Validated @RequestBody RequestCustomerRegisterDTO request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		customerService.createCustomer(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/api/customers/login")
	public ResponseEntity<Boolean> loginCustomer(@RequestBody RequestCustomerLoginDTO request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		boolean body = customerService.loginCustomer(request);
		return ResponseEntity.ok(body);
	}

}
