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
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.product.category.domain.dto.request.RequestCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	// 유저 페이지

	/**
	 * html 헤더에 표시할 카테고리 리스트
	 */
	@GetMapping("/api/categories")
	public ResponseEntity<List<ResponseCategoryDTO>> getCategoriesToDepth3() {
		List<ResponseCategoryDTO> response = categoryService.getCategoriesToDepth3();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 도서 리스트 조회 시 사이드 바에 표시할 카테고리 리스트
	 */
	@GetMapping("/api/categories/{categoryId}")
	public ResponseEntity<List<ResponseCategoryDTO>> getCategoriesById(@PathVariable Long categoryId) {
		List<ResponseCategoryDTO> response = categoryService.getCategoriesById(categoryId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	// 관리자 페이지

	/**
	 * 관리자 페이지에서 전체 카테고리 리스트 조회
	 */
	// @Admin
	@GetMapping("/api/admin/categories")
	public ResponseEntity<List<ResponseCategoryDTO>> getCategories() {
		List<ResponseCategoryDTO> response = categoryService.getCategories();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 최상위 + 하위 카테고리 저장
	 */
	// @Admin
	@PostMapping("/api/admin/categories")
	public ResponseEntity<Void> createCategoryTree(@RequestBody List<RequestCategoryDTO> request) {
		categoryService.createCategoryTree(request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 이미 존재하는 카테고리에 자식 카테고리 저장
	 */
	// @Admin
	@PostMapping("/api/admin/categories/{categoryId}")
	public ResponseEntity<Void> createChildCategory(@PathVariable Long categoryId,
		@RequestBody RequestCategoryDTO request) {
		categoryService.createChildCategory(categoryId, request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 카테고리 수정
	 */
	// @Admin
	@PutMapping("/api/admin/categories/{categoryId}")
	public ResponseEntity<Void> updateCategory(@PathVariable Long categoryId,
		@RequestBody RequestCategoryDTO request) {
		categoryService.updateCategory(categoryId, request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 카테고리 삭제
	 */
	// @Admin
	@DeleteMapping("/api/admin/categories/{categoryId}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
		categoryService.deleteCategory(categoryId);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * 헤더 카테고리 캐싱 삭제 (카테고리 DB 수정이 일어난 경우)
	 */
	// @Admin
	// @DeleteMapping("/api/admin/categories/headerCaching")
	// public ResponseEntity<Void> headerCachingClear() {
	// 	categoryService.clearCategoriesCache();
	//
	// 	return ResponseEntity.status(HttpStatus.OK).build();
	// }
}
