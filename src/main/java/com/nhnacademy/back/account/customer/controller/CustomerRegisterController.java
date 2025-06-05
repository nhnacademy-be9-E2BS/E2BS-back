package com.nhnacademy.back.account.customer.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerRegisterDTO;
import com.nhnacademy.back.account.customer.domain.dto.response.ResponseCustomerDTO;
import com.nhnacademy.back.account.customer.service.CustomerService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers/register")
public class CustomerRegisterController {

	private final CustomerService customerService;

	/**
	 * 비회원 등록
	 */
	@PostMapping
	public ResponseEntity<ResponseCustomerDTO> registerCustomer(
		@Validated @RequestBody RequestCustomerRegisterDTO requestCustomerRegisterDTO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		ResponseCustomerDTO customer = customerService.postCustomerRegister(requestCustomerRegisterDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(customer);
	}

	/**
	 * 중복 아이디 검증
	 */
	@GetMapping("/{customerEmail}")
	public ResponseEntity<Map<String, Boolean>> checkCustomerEmail(@PathVariable String customerEmail) {
		boolean isDuplicateCheck = customerService.isExistsCustomerEmail(customerEmail);

		Map<String, Boolean> response = Collections.singletonMap("available", !isDuplicateCheck);
		return ResponseEntity.ok(response);
	}

}
