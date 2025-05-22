package com.nhnacademy.back.order.wrapper.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestModifyWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestRegisterWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.response.ResponseWrapperDTO;
import com.nhnacademy.back.order.wrapper.service.WrapperService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WrapperController {
	private final WrapperService wrapperService;

	/**
	 * Wrapper 리스트 조회 (판매 중)
	 */
	@Member
	@GetMapping("/api/wrappers")
	public ResponseEntity<Page<ResponseWrapperDTO>> getWrappersBySaleable(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponseWrapperDTO> wrappers = wrapperService.getWrappersBySaleable(true, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(wrappers);
	}

	/**
	 * Wrapper 리스트 조회 (모두)
	 */
	@Admin
	@GetMapping("/api/admin/wrappers")
	public ResponseEntity<Page<ResponseWrapperDTO>> getWrappers(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		log.info("포장지 전체 조회 요청 컨트롤러 start");
		Page<ResponseWrapperDTO> wrappers = wrapperService.getWrappers(pageable);
		log.info("포장지 컨트롤러 응답");
		log.info(wrappers.toString());
		return ResponseEntity.status(HttpStatus.OK).body(wrappers);
	}

	/**
	 * Wrapper 저장
	 */
	@Admin
	@PostMapping("/api/admin/wrappers")
	public ResponseEntity<Void> createWrapper(@RequestBody RequestRegisterWrapperDTO request) {
		wrapperService.createWrapper(request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Wrapper 수정
	 */
	@Admin
	@PutMapping("/api/admin/wrappers/{wrapperId}")
	public ResponseEntity<Void> updateWrapper(@PathVariable Long wrapperId,
		@RequestBody RequestModifyWrapperDTO request) {
		wrapperService.updateWrapper(wrapperId, request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
