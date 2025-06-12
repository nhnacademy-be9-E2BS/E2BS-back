package com.nhnacademy.back.product.publisher.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;
import com.nhnacademy.back.product.publisher.service.PublisherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "출판사", description = "출판사 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/admin/publishers")
public class PublisherController {
	private final PublisherService publisherService;

	/**
	 * Publisher 리스트 조회
	 */
	@Operation(summary = "모든 출판사 리스트 조회",
		description = "관리자 페이지에서 모든 출판사 리스트를 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@Admin
	@GetMapping
	public ResponseEntity<Page<ResponsePublisherDTO>> getPublishers(
		@Parameter(description = "페이징 정보") @PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponsePublisherDTO> response = publisherService.getPublishers(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Publisher 저장
	 */
	@Operation(summary = "출판사 등록",
		description = "관리자 페이지에서 출판사를 등록합니다.",
		responses = {
			@ApiResponse(responseCode = "302", description = "출판사 등록 후 출판사 조회 페이지로 리다이렉션"),
			@ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PostMapping
	public ResponseEntity<Void> createPublisher(
		@Parameter(description = "출판사 등록 및 수정 DTO", required = true, schema = @Schema(implementation = RequestPublisherDTO.class)) @RequestBody RequestPublisherDTO request) {
		publisherService.createPublisher(request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Publisher 수정
	 */
	@Operation(summary = "출판사 수정",
		description = "관리자 페이지에서 출판사명을 수정합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "출판사명 수정 성공"),
			@ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PutMapping("/{publisher-id}")
	public ResponseEntity<Void> updatePublisher(
		@Parameter(description = "수정할 출판사 ID", example = "1", required = true) @PathVariable("publisher-id") Long publisherId,
		@Parameter(description = "출판사 등록 및 수정 DTO", required = true, schema = @Schema(implementation = RequestPublisherDTO.class)) @RequestBody RequestPublisherDTO request) {
		publisherService.updatePublisher(publisherId, request);

		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
