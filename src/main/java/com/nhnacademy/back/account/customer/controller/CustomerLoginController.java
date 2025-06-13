package com.nhnacademy.back.account.customer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.customer.domain.dto.request.RequestCustomerLoginDTO;
import com.nhnacademy.back.account.customer.domain.dto.response.ResponseCustomerDTO;
import com.nhnacademy.back.account.customer.service.CustomerService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Customer Login", description = "비회원 로그인 관련 API")
public class CustomerLoginController {

	private final CustomerService customerService;


	@Operation(summary = "비회원 로그인",
		description = "이메일과 비밀번호를 이용한 비회원 로그인 요청을 처리합니다.",
		responses = {
			@ApiResponse(responseCode = "201", description = "비회원 로그인 성공", content = @Content(schema = @Schema(implementation = ResponseCustomerDTO.class))),
			@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@PostMapping("/api/customers/login")
	public ResponseEntity<ResponseCustomerDTO> customerLogin(@Parameter(description = "비회원 로그인 요청 DTO", required = true) @Valid @RequestBody RequestCustomerLoginDTO requestCustomerLoginDTO,
		                                                     @Parameter(hidden = true) BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		ResponseCustomerDTO customer = customerService.postCustomerLogin(requestCustomerLoginDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(customer);
	}

}
