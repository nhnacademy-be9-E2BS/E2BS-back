package com.nhnacademy.back.account.address.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.address.domain.dto.request.RequestMemberAddressSaveDTO;
import com.nhnacademy.back.account.address.service.MemberAddressService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/members/{memberId}/addresses/save")
public class MemberAddressSaveController {

	private final MemberAddressService memberAddressService;

	@PostMapping
	public ResponseEntity<Void> saveMemberAddress(@PathVariable("memberId") String memberId,
		@Validated @ModelAttribute RequestMemberAddressSaveDTO requestMemberAddressSaveDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		memberAddressService.saveMemberAddress(memberId, requestMemberAddressSaveDTO);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
