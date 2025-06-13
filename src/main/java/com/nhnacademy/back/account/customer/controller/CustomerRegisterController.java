package com.nhnacademy.back.account.customer.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
@RequestMapping("/api/customers/register")
@Tag(name = "Customer Register", description = "비회원 등록 관련 API")
public class CustomerRegisterController {

	private final CustomerService customerService;


	@Operation(summary = "비회원 등록",
		description = "비회원의 이메일, 이름, 비밀번호 등을 입력하여 등록을 수행합니다.",
		responses = {
			@ApiResponse(responseCode = "201", description = "비회원 등록 성공", content = @Content(schema = @Schema(implementation = ResponseCustomerDTO.class))),
			@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@PostMapping
	public ResponseEntity<ResponseCustomerDTO> registerCustomer(@Parameter(description = "비회원 등록 요청 DTO", required = true) @Valid @RequestBody RequestCustomerRegisterDTO requestCustomerRegisterDTO,
		                                                        @Parameter(hidden = true) BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		ResponseCustomerDTO customer = customerService.postCustomerRegister(requestCustomerRegisterDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(customer);
	}

	@Operation(summary = "비회원 이메일 중복 확인", description = "비회원 이메일 중복 여부를 확인합니다. true는 사용 가능하지 않음을 의미합니다.")
	@ApiResponse(responseCode = "200", description = "중복 확인 성공", content = @Content(schema = @Schema(example = "{\"available\": true}")))
	@GetMapping("/{customerEmail}")
	public ResponseEntity<Map<String, Boolean>> checkCustomerEmail(@Parameter(description = "중복 확인할 이메일 주소", required = true, example = "guest@example.com")
		                                                           @PathVariable String customerEmail) {
		boolean isDuplicateCheck = customerService.isExistsCustomerEmail(customerEmail);
		Map<String, Boolean> response = Collections.singletonMap("available", !isDuplicateCheck);
		return ResponseEntity.ok(response);
	}

}