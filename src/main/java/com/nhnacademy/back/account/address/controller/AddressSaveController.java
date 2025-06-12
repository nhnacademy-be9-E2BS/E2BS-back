package com.nhnacademy.back.account.address.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.address.domain.dto.request.RequestMemberAddressSaveDTO;
import com.nhnacademy.back.account.address.exception.SaveAddressFailedException;
import com.nhnacademy.back.account.address.service.AddressService;
import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "마이페이지 배송지 저장 API", description = "회원의 배송지 정보 저장 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/mypage/{member-id}/addresses/form")
public class AddressSaveController {

	private final AddressService addressService;

	/**
	 * 회원의 신규 배송지를 저장하는 메서드
	 */
	@Operation(summary = "회원 배송지 정보 저장", description = "회원의 배송지 정보 저장 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원의 배송지 정보 저장 성공"),
			@ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class))),
			@ApiResponse(responseCode = "500", description = "회원의 배송지 정보 저장 실패", content = @Content(schema = @Schema(implementation = SaveAddressFailedException.class)))
		})
	@Member
	@PostMapping
	public ResponseEntity<Void> saveMemberAddress(@PathVariable("member-id") String memberId,
		@Validated @Parameter(description = "배송지 저장 요청 DTO", required = true, schema = @Schema(implementation = RequestMemberAddressSaveDTO.class))
		@RequestBody RequestMemberAddressSaveDTO requestMemberAddressSaveDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		addressService.saveMemberAddress(memberId, requestMemberAddressSaveDTO);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
