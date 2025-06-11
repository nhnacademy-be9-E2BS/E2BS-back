package com.nhnacademy.back.home.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.home.model.dto.response.ResponseHomeMemberNameDTO;
import com.nhnacademy.back.home.service.HomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/home/{member-id}")
public class HomeController {

	private final HomeService homeService;

	/**
	 * 메인 화면에서 회원 이름 가져오는 메서드
	 */
	@GetMapping
	public ResponseEntity<ResponseHomeMemberNameDTO> getHomeMemberName(@PathVariable("member-id") String memberId) {
		ResponseHomeMemberNameDTO response = homeService.getHomeMemberName(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
