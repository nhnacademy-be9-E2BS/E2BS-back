package com.nhnacademy.back.elasticsearch.controller;

import java.util.List;

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
import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;
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
		@PageableDefault(page = 0, size = 10) Pageable pageable,
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
		@PageableDefault(page = 0, size = 10) Pageable pageable,
		@PathVariable Long categoryId, @RequestParam(required = false) ProductSortType sort) {
		Page<Long> productIds = productSearchService.getProductIdsByCategoryId(pageable, categoryId, sort);
		Page<ResponseProductReadDTO> response = productService.getProductsToElasticSearch(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 메인페이지 베스트 도서
	 */
	@GetMapping("/main/best")
	public ResponseEntity<List<ResponseMainPageProductDTO>> getProductsByMainBest() {
		List<Long> productIds = productSearchService.getBestProductIds();
		List<ResponseMainPageProductDTO> response = productService.getProductsToMain(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 메인페이지 신상 도서
	 */
	@GetMapping("/main/newest")
	public ResponseEntity<List<ResponseMainPageProductDTO>> getProductsByMainNewest() {
		List<Long> productIds = productSearchService.getNewProductIds();
		List<ResponseMainPageProductDTO> response = productService.getProductsToMain(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 헤더에서 베스트(인기) 누르면 도서 여러권 페이징 처리하여 조회
	 */
	@GetMapping("/best")
	public ResponseEntity<Page<ResponseProductReadDTO>> getBestProducts(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<Long> productIds = productSearchService.getBestProductIdsHeader(pageable);
		Page<ResponseProductReadDTO> response = productService.getProductsToElasticSearch(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 헤더에서 신상 누르면 도서 여러권 페이징 처리하여 조회
	 */
	@GetMapping("/newest")
	public ResponseEntity<Page<ResponseProductReadDTO>> getNewestProducts(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<Long> productIds = productSearchService.getNewProductIdsHeader(pageable);
		Page<ResponseProductReadDTO> response = productService.getProductsToElasticSearch(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
