package com.nhnacademy.back.account.memberrank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.memberrank.domain.dto.response.ResponseMemberRankDTO;
import com.nhnacademy.back.account.memberrank.service.MemberRankService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/{memberId}/rank")
public class MemberRankController {

	private final MemberRankService memberRankService;

	@GetMapping
	public ResponseEntity<List<ResponseMemberRankDTO>> getMemberRankService(@PathVariable("memberId") String memberId) {
		List<ResponseMemberRankDTO> response = memberRankService.getMemberRanks();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
