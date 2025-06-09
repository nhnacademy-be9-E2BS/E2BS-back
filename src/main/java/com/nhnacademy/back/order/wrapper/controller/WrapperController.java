package com.nhnacademy.back.order.wrapper.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestModifyWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestRegisterWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestRegisterWrapperMetaDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.response.ResponseWrapperDTO;
import com.nhnacademy.back.order.wrapper.service.WrapperService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WrapperController {
	private final WrapperService wrapperService;

	/**
	 * Wrapper 리스트 조회 (판매 중)
	 */
	@GetMapping("/api/wrappers/wrappers")
	public ResponseEntity<Page<ResponseWrapperDTO>> getWrappersBySaleable(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponseWrapperDTO> wrappers = wrapperService.getWrappersBySaleable(true, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(wrappers);
	}

	/**
	 * Wrapper 리스트 조회 (모두)
	 */
	@Admin
	@GetMapping("/api/auth/admin/wrappers")
	public ResponseEntity<Page<ResponseWrapperDTO>> getWrappers(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponseWrapperDTO> wrappers = wrapperService.getWrappers(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(wrappers);
	}

	/**
	 * Wrapper 저장
	 */
	@Admin
	@PostMapping(value = "/api/auth/admin/wrappers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> createWrapper(@RequestPart("requestMeta") RequestRegisterWrapperMetaDTO requestMeta,
		@RequestPart("wrapperImage") MultipartFile wrapperImage) {
		RequestRegisterWrapperDTO request = new RequestRegisterWrapperDTO(requestMeta.getWrapperPrice(),
			requestMeta.getWrapperName(), wrapperImage, requestMeta.isWrapperSaleable());
		wrapperService.createWrapper(request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Wrapper 수정
	 */
	@Admin
	@PutMapping("/api/auth/admin/wrappers/{wrapperId}")
	public ResponseEntity<Void> updateWrapper(@PathVariable Long wrapperId,
		@RequestBody RequestModifyWrapperDTO request) {
		wrapperService.updateWrapper(wrapperId, request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
