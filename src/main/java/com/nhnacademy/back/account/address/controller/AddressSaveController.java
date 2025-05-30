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
import com.nhnacademy.back.account.address.service.AddressService;
import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/mypage/{memberId}/addresses/save")
public class AddressSaveController {

	private final AddressService addressService;

	/**
	 * 회원의 신규 배송지를 저장하는 메서드
	 */
	@Member
	@PostMapping
	public ResponseEntity<Void> saveMemberAddress(@PathVariable("memberId") String memberId,
		@Validated @RequestBody RequestMemberAddressSaveDTO requestMemberAddressSaveDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		addressService.saveMemberAddress(memberId, requestMemberAddressSaveDTO);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
