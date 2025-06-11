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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "도서 검색 및 정렬", description = "엘라스틱 서치 도서 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books/elasticsearch")
public class ProductSearchController {
	private final ProductService productService;
	private final ProductSearchService productSearchService;

	/**
	 * 검색어 도서 여러권 페이징 처리하여 조회 & 정렬은 선택사항
	 */
	@Operation(summary = "검색어로 도서 검색 및 정렬",
		description = "검색어로 도서 리스트 조회 후 정렬 (정렬은 선택사항)",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공"),
			@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content(schema = @Schema(implementation = ProductSortType.class))),
		})
	@GetMapping("/search")
	public ResponseEntity<Page<ResponseProductReadDTO>> getProductsBySearch(
		@Parameter(description = "페이징 정보") @PageableDefault(page = 0, size = 10) Pageable pageable,
		@Parameter(description = "검색 키워드", required = true, in = ParameterIn.QUERY) @RequestParam String keyword,
		@Parameter(description = "회원 아이디", required = true) @RequestParam String memberId,
		@Parameter(description = "정렬 기준", in = ParameterIn.QUERY) @RequestParam(required = false) ProductSortType sort) {
		Page<Long> productIds = productSearchService.getProductIdsBySearch(pageable, keyword, sort);
		Page<ResponseProductReadDTO> response = productService.getProductsToElasticSearch(productIds, memberId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 카테고리별 도서 여러권 페이징 처리하여 조회 & 정렬은 선택사항
	 */
	@Operation(summary = "카테고리 ID로 도서 검색 및 정렬",
		description = "카테고리 헤더를 누르면 해당 도서 리스트 조회 후 정렬 (정렬은 선택사항)",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공"),
			@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content(schema = @Schema(implementation = ProductSortType.class))),
		})
	@GetMapping("/category/{category-id}")
	public ResponseEntity<Page<ResponseProductReadDTO>> getProductsByCategory(
		@Parameter(description = "페이징 정보") @PageableDefault(page = 0, size = 10) Pageable pageable,
		@Parameter(description = "조회할 카테고리 ID", required = true, in = ParameterIn.QUERY) @PathVariable("category-id") Long categoryId,
		@Parameter(description = "회원 아이디", required = true) @RequestParam String memberId,
		@Parameter(description = "정렬 기준", in = ParameterIn.QUERY) @RequestParam(required = false) ProductSortType sort) {
		Page<Long> productIds = productSearchService.getProductIdsByCategoryId(pageable, categoryId, sort);
		Page<ResponseProductReadDTO> response = productService.getProductsToElasticSearch(productIds, memberId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 메인페이지 베스트 도서
	 */
	@Operation(summary = "메인페이지 베스트 도서 조회",
		description = "메인페이지에서 베스트 도서 12권 리스트 조회",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@GetMapping("/main/best")
	public ResponseEntity<List<ResponseMainPageProductDTO>> getProductsByMainBest() {
		List<Long> productIds = productSearchService.getBestProductIds();
		List<ResponseMainPageProductDTO> response = productService.getProductsToMain(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 메인페이지 신상 도서
	 */
	@Operation(summary = "메인페이지 신상 도서 조회",
		description = "메인페이지에서 신상 도서 12권 리스트 조회",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@GetMapping("/main/newest")
	public ResponseEntity<List<ResponseMainPageProductDTO>> getProductsByMainNewest() {
		List<Long> productIds = productSearchService.getNewProductIds();
		List<ResponseMainPageProductDTO> response = productService.getProductsToMain(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 헤더에서 베스트(인기) 누르면 도서 여러권 페이징 처리하여 조회
	 */
	@Operation(summary = "베스트 도서 조회",
		description = "헤더에서 '베스트'를 누르면 베스트 도서 리스트 조회",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@GetMapping("/best")
	public ResponseEntity<Page<ResponseProductReadDTO>> getBestProducts(
		@Parameter(description = "회원 아이디", required = true) @RequestParam String memberId,
		@Parameter(description = "페이징 정보") @PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<Long> productIds = productSearchService.getBestProductIdsHeader(pageable);
		Page<ResponseProductReadDTO> response = productService.getProductsToElasticSearch(productIds, memberId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 헤더에서 신상 누르면 도서 여러권 페이징 처리하여 조회
	 */
	@Operation(summary = "신상 도서 조회",
		description = "헤더에서 '신상'을 누르면 신상 도서 리스트 조회",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@GetMapping("/newest")
	public ResponseEntity<Page<ResponseProductReadDTO>> getNewestProducts(
		@Parameter(description = "회원 아이디", required = true) @RequestParam String memberId,
		@Parameter(description = "페이징 정보") @PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<Long> productIds = productSearchService.getNewProductIdsHeader(pageable);
		Page<ResponseProductReadDTO> response = productService.getProductsToElasticSearch(productIds, memberId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
