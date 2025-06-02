package com.nhnacademy.back.elasticsearch.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.elasticsearch.domain.document.ProductSortType;
import com.nhnacademy.back.elasticsearch.service.ProductSearchService;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books/elasticsearch")
public class ProductSearchController {
	private final ProductService productService;
	private final ProductSearchService productSearchService;

	/**
	 * 검색어 도서 여러권 페이징 처리하여 조회 & 정렬은 선택사항
	 */
	@GetMapping("/search")
	public ResponseEntity<Page<ResponseProductReadDTO>> getProductsBySearch(
		@PageableDefault(page = 0, size = 9) Pageable pageable,
		@RequestParam String keyword, @RequestParam(required = false) ProductSortType sort) {
		Page<Long> productIds = productSearchService.getProductIdsBySearch(pageable, keyword, sort);
		Page<ResponseProductReadDTO> response = productService.getProductsToElasticSearch(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 카테고리별 도서 여러권 페이징 처리하여 조회 & 정렬은 선택사항
	 */
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<Page<ResponseProductReadDTO>> getProductsByCategory(
		@PageableDefault(page = 0, size = 9) Pageable pageable,
		@PathVariable Long categoryId, @RequestParam(required = false) ProductSortType sort) {
		Page<Long> productIds = productSearchService.getProductIdsByCategoryId(pageable, categoryId, sort);
		Page<ResponseProductReadDTO> response = productService.getProductsToElasticSearch(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
