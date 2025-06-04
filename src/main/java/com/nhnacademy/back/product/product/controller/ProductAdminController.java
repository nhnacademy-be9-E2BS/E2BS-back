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
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductCouponDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductsApiSearchDTO;
import com.nhnacademy.back.product.product.service.ProductAPIService;
import com.nhnacademy.back.product.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
public class ProductAdminController {
	private final ProductService productService;
	private final ProductAPIService productApiService;

	/**
	 * 도서 받아와서 DB에 저장
	 * 201 상태코드 반환
	 */
	@Admin
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> createProduct(@RequestPart("requestMeta") RequestProductMetaDTO requestMeta,  @RequestPart("productImage") List<MultipartFile> productImage) {
		RequestProductDTO request = new RequestProductDTO(requestMeta.getProductStateId(), requestMeta.getPublisherId(), requestMeta.getProductTitle(), requestMeta.getProductContent(), requestMeta.getProductDescription(), requestMeta.getProductPublishedAt(), requestMeta.getProductIsbn(), requestMeta.getProductRegularPrice(), requestMeta.getProductSalePrice(), requestMeta.isProductPackageable(), requestMeta.getProductStock(), productImage, requestMeta.getTagIds(), requestMeta.getCategoryIds(), requestMeta.getContributorIds());
		long productId = productService.createProduct(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 전체 도서 페이징 처리하여 조회
	 */
	@Admin
	@GetMapping
	public ResponseEntity<Page<ResponseProductReadDTO>> getProducts(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponseProductReadDTO> response = productService.getProducts(pageable, 0);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * productId와 바꿀 정보들을 받아와 DB에 정보 업데이트
	 * 200 상태코드 반환
	 */
	@Admin
	@PutMapping("/{bookId}")
	public ResponseEntity<Void> updateProduct(@PathVariable Long bookId, @RequestPart("product") RequestProductMetaDTO requestMeta, @RequestPart("productImage") List<MultipartFile> productImage) {
		RequestProductDTO request = new RequestProductDTO(requestMeta.getProductStateId(), requestMeta.getPublisherId(), requestMeta.getProductTitle(), requestMeta.getProductContent(), requestMeta.getProductDescription(), requestMeta.getProductPublishedAt(), requestMeta.getProductIsbn(), requestMeta.getProductRegularPrice(), requestMeta.getProductSalePrice(), requestMeta.isProductPackageable(), requestMeta.getProductStock(), productImage, requestMeta.getTagIds(), requestMeta.getCategoryIds(), requestMeta.getContributorIds());

		productService.updateProduct(bookId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 *  productId와 바꿀 재고를 받아와 업데이트
	 *  200 상태코드 반환
	 */
	@Admin
	@PutMapping("/{bookId}/stock")
	public ResponseEntity<Void> updateProductStock(@PathVariable Long bookId,
		@RequestBody RequestProductStockUpdateDTO request) {
		productService.updateProductStock(bookId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * productId와 바꿀 판매가를 받아와 업데이트
	 * 200 상태코드 반환
	 */
	@Admin
	@PutMapping("/{bookId}/salePrice")
	public ResponseEntity<Void> updateProductSalePrice(@PathVariable Long bookId,
		@RequestBody RequestProductSalePriceUpdateDTO request) {
		productService.updateProductSalePrice(bookId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * sale 상태 도서 전체 조회
	 * 200 상태코드 반환
	 */
	@Admin
	@GetMapping("/status/sale")
	public ResponseEntity<Page<ResponseProductCouponDTO>> getProductsToCoupon(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponseProductCouponDTO> products = productService.getProductsToCoupon(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(products);
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
