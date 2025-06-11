package com.nhnacademy.back.account.address.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.address.domain.dto.request.RequestMemberAddressSaveDTO;
import com.nhnacademy.back.account.address.domain.dto.response.ResponseMemberAddressDTO;
import com.nhnacademy.back.account.address.exception.DeleteAddressFailedException;
import com.nhnacademy.back.account.address.exception.NotFoundAddressException;
import com.nhnacademy.back.account.address.exception.UpdateAddressFailedException;
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

@Tag(name = "마이페이지 배송지 API", description = "회원의 배송지 관리 기능 제공")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/mypage/{member-id}/addresses")
public class AddressController {

	private final AddressService addressService;

	/**
	 * 회원의 저장된 모든 배송지를 가져오는 메서드
	 */
	@Operation(summary = "회원의 배송지 목록 조회", description = "회원의 모든 배송지 목록 조회 기능")
	@Member
	@GetMapping
	public ResponseEntity<List<ResponseMemberAddressDTO>> getMemberAddresses(
		@PathVariable("member-id") String memberId) {
		List<ResponseMemberAddressDTO> responseMemberAddressDTOS = addressService.getMemberAddresses(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberAddressDTOS);
	}

	/**
	 * 회원의 특정 배송지 정보를 가져오는 메서드
	 */
	@Operation(summary = "회원의 특정 배송지 정보 조회", description = "회원의 특정 배송지 정보 조회 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원의 특정 배송지 정보 조회 성공"),
			@ApiResponse(responseCode = "400", description = "회원의 특정 배송지 정보 조회 실패",
				content = @Content(schema = @Schema(implementation = NotFoundAddressException.class)))
		})
	@Member
	@GetMapping("/{address-id}")
	public ResponseEntity<ResponseMemberAddressDTO> getAddress(@PathVariable("member-id") String memberId,
		@PathVariable("address-id") long addressId) {
		ResponseMemberAddressDTO responseMemberAddressDTO = addressService.getAddressByAddressId(
			memberId, addressId
		);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberAddressDTO);
	}

	/**
	 * 회원의 배송지 정보를 수정하는 메서드
	 */
	@Operation(summary = "회원의 특정 배송지 정보 수정", description = "회원의 특정 배송지 정보 수정 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원의 특정 배송지 수정 성공"),
			@ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class))),
			@ApiResponse(responseCode = "500", description = "회원의 특정 배송지 수정 실패", content = @Content(schema = @Schema(implementation = UpdateAddressFailedException.class)))
		})
	@Member
	@PutMapping("/{address-id}")
	public ResponseEntity<Void> updateAddress(@Validated
		@Parameter(description = "배송지 저장 요청 DTO", required = true, schema = @Schema(implementation = RequestMemberAddressSaveDTO.class))
		@RequestBody RequestMemberAddressSaveDTO requestMemberAddressSaveDTO,
		BindingResult bindingResult,
		@PathVariable("member-id") String memberId,
		@PathVariable("address-id") long addressId) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		addressService.updateAddress(requestMemberAddressSaveDTO, memberId, addressId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 회원의 배송지 정보를 삭제하는 메서드
	 */
	@Operation(summary = "회원의 배송지 정보 삭제", description = "회원의 특정 배송지 정보 삭제 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원의 특정 배송지 삭제 성공 응답"),
			@ApiResponse(responseCode = "500", description = "회원의 특정 배송지 삭제 실패", content = @Content(schema = @Schema(implementation = DeleteAddressFailedException.class)))
		})
	@Member
	@DeleteMapping("/{address-id}")
	public ResponseEntity<Void> deleteAddress(@PathVariable("member-id") String memberId,
		@PathVariable("address-id") long addressId) {
		addressService.deleteAddress(memberId, addressId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 회원의 기본 배송지를 수정하는 메서드
	 */
	@Operation(summary = "회원의 기본 배송지 설정", description = "회원의 기본 배송지 설정 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원의 기본 배송지 설정 성공 응답"),
			@ApiResponse(responseCode = "500", description = "회원의 기본 배송지 설정 실패", content = @Content(schema = @Schema(implementation = UpdateAddressFailedException.class)))
		})
	@Member
	@PostMapping("/{address-id}/default")
	public ResponseEntity<Void> setDefaultAddress(@PathVariable("member-id") String memberId,
		@PathVariable("address-id") long addressId) {
		addressService.setDefaultAddress(memberId, addressId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
