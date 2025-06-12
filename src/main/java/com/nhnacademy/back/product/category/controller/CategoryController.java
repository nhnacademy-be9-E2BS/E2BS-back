package com.nhnacademy.back.product.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.product.category.domain.dto.request.RequestCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryIdsDTO;
import com.nhnacademy.back.product.category.service.CategoryService;
import com.nhnacademy.back.product.category.service.ProductCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "카테고리", description = "관리자 카테고리 관련 API")
@RestController
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;
	private final ProductCategoryService productCategoryService;

	// 유저 페이지

	/**
	 * html 헤더에 표시할 카테고리 리스트
	 */
	@Operation(summary = "헤더에 표시할 카테고리 리스트 조회",
		description = "헤더에서 3단계까지의 카테고리 리스트를 폴더 구조로 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@GetMapping("/api/categories")
	public ResponseEntity<List<ResponseCategoryDTO>> getCategoriesToDepth3() {
		List<ResponseCategoryDTO> response = categoryService.getCategoriesToDepth3();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 전체 카테고리 리스트 조회
	 */
	@Operation(summary = "모든 카테고리 리스트 조회",
		description = "관리자 페이지에서 모든 카테고리 리스트를 폴더 구조로 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@GetMapping("/api/categories/all")
	public ResponseEntity<List<ResponseCategoryDTO>> getAllCategories() {
		List<ResponseCategoryDTO> response = categoryService.getCategories();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * product id 리스트로 각 product의 category id 리스트 조회
	 */
	@Operation(summary = "상품의 카테고리 ID 리스트 조회",
		description = "주문 페이지에서 쿠폰 적용을 위해 각 도서의 카테고리 ID 리스트를 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@GetMapping("/api/categories/productIds")
	public ResponseEntity<List<ResponseCategoryIdsDTO>> getCategoriesByProductIds(
		@Parameter(description = "도서 ID 리스트", required = true) @RequestParam List<Long> productIds) {
		List<ResponseCategoryIdsDTO> response = productCategoryService.getCategoriesByProductId(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	// 관리자 페이지

	/**
	 * 최상위 + 하위 카테고리 저장
	 */
	@Operation(summary = "최상위, 하위 카테고리 등록",
		description = "관리자 페이지에서 최상위 카테고리와 그 하위 카테고리를 등록합니다.",
		responses = {
			@ApiResponse(responseCode = "302", description = "카테고리 등록 후 카테고리 리스트 조회 페이지로 리다이렉션"),
			@ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PostMapping("/api/auth/admin/categories")
	public ResponseEntity<Void> createCategoryTree(
		@Parameter(description = "등록할 카테고리 리스트", required = true) @RequestBody List<RequestCategoryDTO> request) {
		categoryService.createCategoryTree(request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 이미 존재하는 카테고리에 자식 카테고리 저장
	 */
	@Operation(summary = "하위 카테고리 등록",
		description = "관리자 페이지에서 이미 존재하는 카테고리의 하위 카테고리를 등록합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "카테고리 등록 성공"),
			@ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PostMapping("/api/auth/admin/categories/{category-id}")
	public ResponseEntity<Void> createChildCategory(
		@Parameter(description = "등록할 카테고리의 상위 카테고리 ID", example = "1", required = true) @PathVariable("category-id") Long categoryId,
		@Parameter(description = "카테고리 등록 및 수정 모델", required = true, schema = @Schema(implementation = RequestCategoryDTO.class)) @RequestBody RequestCategoryDTO request) {
		categoryService.createChildCategory(categoryId, request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 카테고리 수정
	 */
	@Operation(summary = "카테고리 수정",
		description = "관리자 페이지에서 카테고리의 이름을 수정합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "카테고리명 수정 성공"),
			@ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PutMapping("/api/auth/admin/categories/{category-id}")
	public ResponseEntity<Void> updateCategory(
		@Parameter(description = "수정할 카테고리 ID", example = "1", required = true) @PathVariable("category-id") Long categoryId,
		@Parameter(description = "카테고리 등록 및 수정 모델", required = true, schema = @Schema(implementation = RequestCategoryDTO.class)) @RequestBody RequestCategoryDTO request) {
		categoryService.updateCategory(categoryId, request);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * 카테고리 삭제
	 */
	@Operation(summary = "카테고리 삭제",
		description = "관리자 페이지에서 최하위 카테고리를 삭제합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "카테고리 삭제 성공")
		})
	@Admin
	@DeleteMapping("/api/auth/admin/categories/{category-id}")
	public ResponseEntity<Void> deleteCategory(
		@Parameter(description = "삭제할 카테고리 ID", example = "5", required = true) @PathVariable("category-id") Long categoryId) {
		categoryService.deleteCategory(categoryId);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
