package com.nhnacademy.back.account.address.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.address.domain.dto.response.ResponseMemberAddressDTO;
import com.nhnacademy.back.account.address.service.MemberAddressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/members/{memberId}/addresses")
public class MemberAddressController {

	private final MemberAddressService memberAddressService;

	@GetMapping
	public ResponseEntity<List<ResponseMemberAddressDTO>> getMemberAddresses(
		@PathVariable("memberId") String memberId) {
		List<ResponseMemberAddressDTO> responseMemberAddressDTOS = memberAddressService.getMemberAddresses(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMemberAddressDTOS);
	}

}
