package com.nhnacademy.back.product.product.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateByQueryDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiSearchByQueryTypeDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiSearchDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductMetaDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.UnifiedProductApiSearchDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductApiSearchByQueryTypeDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductsApiSearchDTO;
import com.nhnacademy.back.product.product.service.ProductAPIService;
import com.nhnacademy.back.product.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "도서(관리자)", description = "관리자 도서 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/admin/books")
public class ProductAdminController {
	private final ProductService productService;
	private final ProductAPIService productApiService;

	/**
	 * 도서 받아와서 DB에 저장
	 * 201 상태코드 반환
	 */
	@Operation(summary = "도서 등록",
		description = "관리자 페이지에서 도서를 등록합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공"),
			@ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> createProduct(
		@Parameter(description = "도서 등록 및 수정 DTO", required = true, schema = @Schema(implementation = RequestProductDTO.class))
		@RequestPart("requestMeta") RequestProductMetaDTO requestMeta,
		@Parameter(description = "도서 이미지 리스트", required = true) @RequestPart("productImage") List<MultipartFile> productImage) {
		RequestProductDTO request = new RequestProductDTO(requestMeta.getProductStateId(), requestMeta.getPublisherId(),
			requestMeta.getProductTitle(), requestMeta.getProductContent(), requestMeta.getProductDescription(),
			requestMeta.getProductPublishedAt(), requestMeta.getProductIsbn(), requestMeta.getProductRegularPrice(),
			requestMeta.getProductSalePrice(), requestMeta.isProductPackageable(), requestMeta.getProductStock(),
			productImage, requestMeta.getTagIds(), requestMeta.getCategoryIds(), requestMeta.getContributorIds());
		productService.createProduct(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 전체 도서 페이징 처리하여 조회
	 */
	@Operation(summary = "모든 도서 리스트 조회",
		description = "관리자 페이지에서 모든 도서 리스트를 조회합니다. (검색 가능)",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@Admin
	@GetMapping
	public ResponseEntity<Page<ResponseProductReadDTO>> getProducts(
		@Parameter(description = "페이징 정보") @PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponseProductReadDTO> response = productService.getProducts(pageable, 0);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * productId와 바꿀 정보들을 받아와 DB에 정보 업데이트
	 * 200 상태코드 반환
	 */
	@Operation(summary = "도서 수정",
		description = "관리자 페이지에서 도서를 수정합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공"),
			@ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PutMapping("/{book-id}")
	public ResponseEntity<Void> updateProduct(
		@Parameter(description = "수정할 도서 ID", example = "1", required = true) @PathVariable("book-id") Long bookId,
		@Parameter(description = "도서 등록 및 수정 DTO", required = true, schema = @Schema(implementation = RequestProductDTO.class)) @RequestPart("product") RequestProductMetaDTO requestMeta,
		@Parameter(description = "도서 이미지 리스트", required = true) @RequestPart("productImage") List<MultipartFile> productImage) {
		RequestProductDTO request = new RequestProductDTO(requestMeta.getProductStateId(), requestMeta.getPublisherId(),
			requestMeta.getProductTitle(), requestMeta.getProductContent(), requestMeta.getProductDescription(),
			requestMeta.getProductPublishedAt(), requestMeta.getProductIsbn(), requestMeta.getProductRegularPrice(),
			requestMeta.getProductSalePrice(), requestMeta.isProductPackageable(), requestMeta.getProductStock(),
			productImage, requestMeta.getTagIds(), requestMeta.getCategoryIds(), requestMeta.getContributorIds());

		productService.updateProduct(bookId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 *  productId와 바꿀 재고를 받아와 업데이트
	 *  200 상태코드 반환
	 */
	@Operation(summary = "도서 재고 수정",
		description = "주문 완료 및 주문 취소 시 도서의 재고를 수정합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공"),
			@ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PutMapping("/{book-id}/stock")
	public ResponseEntity<Void> updateProductStock(
		@Parameter(description = "수정할 도서 ID", example = "1", required = true) @PathVariable("book-id") Long bookId,
		@Parameter(description = "도서 재고 수정 DTO", required = true, schema = @Schema(implementation = RequestProductStockUpdateDTO.class)) @RequestBody RequestProductStockUpdateDTO request) {
		productService.updateProductStock(bookId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * productId와 바꿀 판매가를 받아와 업데이트
	 * 200 상태코드 반환
	 */
	@Operation(summary = "도서 판매가 수정",
		description = "관리자 페이지에서 도서의 판매가를 수정합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공"),
			@ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class)))
		})
	@Admin
	@PutMapping("/{book-id}/salePrice")
	public ResponseEntity<Void> updateProductSalePrice(
		@Parameter(description = "수정할 도서 ID", example = "1", required = true) @PathVariable("book-id") Long bookId,
		@Parameter(description = "도서 판매가 수정 DTO", required = true, schema = @Schema(implementation = RequestProductSalePriceUpdateDTO.class)) @RequestBody RequestProductSalePriceUpdateDTO request) {
		productService.updateProductSalePrice(bookId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 *
	 * 검색어와 검색타입으로 책 검색
	 */

	@Admin
	@GetMapping("/aladdin/search")
	public ResponseEntity<?> searchProducts(@ModelAttribute UnifiedProductApiSearchDTO request, Pageable pageable) {
		boolean isQueryProvided = request.getQuery() != null && !request.getQuery().isBlank();

		if (isQueryProvided) { //검색어 + 검색타입
			RequestProductApiSearchDTO searchDTO = new RequestProductApiSearchDTO();
			searchDTO.setQuery(request.getQuery());
			searchDTO.setQueryType(request.getQueryType());

			Page<ResponseProductsApiSearchDTO> products = productApiService.searchProducts(searchDTO, pageable);
			return ResponseEntity.status(HttpStatus.OK).body(products);

		} else { //베스트셀러, 신간등 리스트로 검색하기
			RequestProductApiSearchByQueryTypeDTO showDTO = new RequestProductApiSearchByQueryTypeDTO();
			showDTO.setQueryType(request.getQueryType());

			Page<ResponseProductApiSearchByQueryTypeDTO> products = productApiService.searchProductsByQuery(showDTO,
				pageable);
			return ResponseEntity.status(HttpStatus.OK).body(products);

		}
	}

	/**
	 * api 사용해서 등록
	 */
	@Admin
	@PostMapping("/aladdin/register")
	public ResponseEntity<Void> createProductByApi(@RequestBody RequestProductApiCreateDTO request) {
		productApiService.createProduct(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Admin
	@PostMapping("/aladdin/register/list")
	public ResponseEntity<Void> createProductQueryByApi(@RequestBody RequestProductApiCreateByQueryDTO request) {
		productApiService.createProductByQuery(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
