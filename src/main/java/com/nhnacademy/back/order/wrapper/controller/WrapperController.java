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
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestModifyWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestRegisterWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestRegisterWrapperMetaDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.response.ResponseWrapperDTO;
import com.nhnacademy.back.order.wrapper.service.WrapperService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "포장지", description = "포장지 관련 API")
@RestController
@RequiredArgsConstructor
public class WrapperController {
	private final WrapperService wrapperService;

	/**
	 * Wrapper 리스트 조회 (판매 중)
	 */
	@Operation(summary = "판매 중인 포장지 리스트 조회",
		description = "주문 페이지에서 판매 상태인 포장지 리스트를 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@GetMapping("/api/wrappers")
	public ResponseEntity<Page<ResponseWrapperDTO>> getWrappersBySaleable(
		@Parameter(description = "페이징 정보") @PageableDefault(page = 0, size = 1000) Pageable pageable) {
		Page<ResponseWrapperDTO> wrappers = wrapperService.getWrappersBySaleable(true, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(wrappers);
	}

	/**
	 * Wrapper 리스트 조회 (모두)
	 */
	@Operation(summary = "모든 포장지 리스트 조회",
		description = "관리자 페이지에서 모든 포장지 리스트를 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@Admin
	@GetMapping("/api/auth/admin/wrappers")
	public ResponseEntity<Page<ResponseWrapperDTO>> getWrappers(
		@Parameter(description = "페이징 정보") @PageableDefault(page = 0, size = 5) Pageable pageable) {
		Page<ResponseWrapperDTO> wrappers = wrapperService.getWrappers(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(wrappers);
	}

	/**
	 * Wrapper 저장
	 */
	@Operation(summary = "포장지 등록",
		description = "관리자 페이지에서 포장지를 등록합니다.",
		responses = {
			@ApiResponse(responseCode = "302", description = "포장지 등록 후 포장지 조회 페이지로 리다이렉션"),
			@ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PostMapping(value = "/api/auth/admin/wrappers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> createWrapper(
		@Parameter(description = "포장지 등록 DTO", required = true, schema = @Schema(implementation = RequestRegisterWrapperDTO.class))
		@RequestPart("requestMeta") RequestRegisterWrapperMetaDTO requestMeta,
		@Parameter(description = "포장지 이미지", required = true, schema = @Schema(implementation = MultipartFile.class))
		@RequestPart("wrapperImage") MultipartFile wrapperImage) {
		RequestRegisterWrapperDTO request = new RequestRegisterWrapperDTO(requestMeta.getWrapperPrice(),
			requestMeta.getWrapperName(), wrapperImage, requestMeta.isWrapperSaleable());
		wrapperService.createWrapper(request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Wrapper 수정
	 */
	@Operation(summary = "포장지 수정",
		description = "관리자 페이지에서 포장지의 판매 여부를 수정합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "포장지 수정 성공"),
			@ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PutMapping("/api/auth/admin/wrappers/{wrapper-id}")
	public ResponseEntity<Void> updateWrapper(
		@Parameter(description = "수정할 포장지 ID", example = "1", required = true) @PathVariable("wrapper-id") Long wrapperId,
		@Parameter(description = "포장지 수정 DTO", required = true, schema = @Schema(implementation = RequestModifyWrapperDTO.class))
		@RequestBody RequestModifyWrapperDTO request) {
		wrapperService.updateWrapper(wrapperId, request);

		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
