package com.nhnacademy.back.account.address.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.address.domain.dto.request.RequestMemberAddressSaveDTO;
import com.nhnacademy.back.account.address.domain.dto.response.ResponseMemberAddressDTO;
import com.nhnacademy.back.account.address.service.AddressService;
import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/mypage/{memberId}/addresses")
public class AddressController {

	private final AddressService addressService;

	/**
	 * 회원의 저장된 모든 배송지를 가져오는 메서드
	 */
	@Member
	@GetMapping
	public ResponseEntity<List<ResponseMemberAddressDTO>> getMemberAddresses(
		@PathVariable("memberId") String memberId) {
		List<ResponseMemberAddressDTO> responseMemberAddressDTOS = addressService.getMemberAddresses(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberAddressDTOS);
	}

	/**
	 * 회원의 특정 배송지 정보를 가져오는 메서드
	 */
	@Member
	@GetMapping("/{addressId}")
	public ResponseEntity<ResponseMemberAddressDTO> getAddress(@PathVariable("memberId") String memberId,
		@PathVariable("addressId") long addressId) {
		ResponseMemberAddressDTO responseMemberAddressDTO = addressService.getAddressByAddressId(
			memberId, addressId
		);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberAddressDTO);
	}

	/**
	 * 회원의 배송지 정보를 수정하는 메서드
	 */
	@Member
	@PutMapping("/{addressId}")
	public ResponseEntity<Void> updateAddress(
		@Validated @RequestBody RequestMemberAddressSaveDTO requestMemberAddressSaveDTO,
		BindingResult bindingResult,
		@PathVariable("memberId") String memberId,
		@PathVariable("addressId") long addressId) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		addressService.updateAddress(requestMemberAddressSaveDTO, memberId, addressId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 회원의 배송지 정보를 삭제하는 메서드
	 */
	@Member
	@DeleteMapping("/{addressId}")
	public ResponseEntity<Void> deleteAddress(@PathVariable("memberId") String memberId,
		@PathVariable("addressId") long addressId) {
		addressService.deleteAddress(memberId, addressId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
