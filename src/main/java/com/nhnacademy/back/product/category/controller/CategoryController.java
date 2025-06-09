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
import com.nhnacademy.back.product.category.domain.dto.request.RequestCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryIdsDTO;
import com.nhnacademy.back.product.category.service.CategoryService;
import com.nhnacademy.back.product.category.service.ProductCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;
	private final ProductCategoryService productCategoryService;

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
	 * 전체 카테고리 리스트 조회
	 */
	@GetMapping("/api/categories/all")
	public ResponseEntity<List<ResponseCategoryDTO>> getAllCategories() {
		List<ResponseCategoryDTO> response = categoryService.getCategories();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * product id 리스트로 각 product의 category id 리스트 조회
	 */
	@GetMapping("/api/categories/productIds")
	public ResponseEntity<List<ResponseCategoryIdsDTO>> getCategoriesByProductIds(@RequestParam List<Long> productIds) {
		List<ResponseCategoryIdsDTO> response = productCategoryService.getCategoriesByProductId(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	// 관리자 페이지

	/**
	 * 최상위 + 하위 카테고리 저장
	 */
	@Admin
	@PostMapping("/api/auth/admin/categories")
	public ResponseEntity<Void> createCategoryTree(@RequestBody List<RequestCategoryDTO> request) {
		categoryService.createCategoryTree(request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 이미 존재하는 카테고리에 자식 카테고리 저장
	 */
	@Admin
	@PostMapping("/api/auth/admin/categories/{categoryId}")
	public ResponseEntity<Void> createChildCategory(@PathVariable Long categoryId,
		@RequestBody RequestCategoryDTO request) {
		categoryService.createChildCategory(categoryId, request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 카테고리 수정
	 */
	@Admin
	@PutMapping("/api/auth/admin/categories/{categoryId}")
	public ResponseEntity<Void> updateCategory(@PathVariable Long categoryId,
		@RequestBody RequestCategoryDTO request) {
		categoryService.updateCategory(categoryId, request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 카테고리 삭제
	 */
	@Admin
	@DeleteMapping("/api/auth/admin/categories/{categoryId}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
		categoryService.deleteCategory(categoryId);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
