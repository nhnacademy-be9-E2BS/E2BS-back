package com.nhnacademy.back.product.product.kim.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.product.category.service.ProductCategoryService;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiSearchDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductCouponDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductsApiSearchDTO;
import com.nhnacademy.back.product.product.kim.service.ProductService;
import com.nhnacademy.back.product.product.park.service.ProductAPIService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
public class ProductAdminController {
	private final ProductService productService;
	private final ProductAPIService productApiService;
	private final ProductCategoryService productCategoryService;

	/**
	 * 도서 받아와서 DB에 저장
	 * 201 상태코드 반환
	 */
	@Admin
	@PostMapping
	public ResponseEntity<Void> createProduct(@RequestBody RequestProductDTO request) {
		long productId = productService.createProduct(request);
		productCategoryService.createProductCategory(productId, request.getCategoryIds(), false);
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
	 * order전용 productId들을 받아서 정보를 반환
	 * 200 상태코드 반환
	 */
	@Admin
	@GetMapping("/order")
	public ResponseEntity<List<ResponseProductReadDTO>> getProducts(@RequestBody List<Long> products) {
		List<ResponseProductReadDTO> productsDTO = productService.getProducts(products);
		return ResponseEntity.status(HttpStatus.OK).body(productsDTO);
	}

	/**
	 * productId와 바꿀 정보들을 받아와 DB에 정보 업데이트
	 * 200 상태코드 반환
	 */
	@Admin
	@PutMapping("/{bookId}")
	public ResponseEntity<Void> updateProduct(@PathVariable Long bookId, @RequestBody RequestProductDTO request) {
		productService.updateProduct(bookId, request);
		productCategoryService.createProductCategory(bookId, request.getCategoryIds(), true);
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
	public ResponseEntity<Page<ResponseProductCouponDTO>> getProductsToCoupon(Pageable pageable) {
		Page<ResponseProductCouponDTO> products = productService.getProductsToCoupon(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(products);
	}

	/**
	 *
	 * 검색어와 검색타입으로 책 검색
	 */

	@Admin
	@GetMapping("/aladdin/search")
	public ResponseEntity<Page<ResponseProductsApiSearchDTO>> searchProducts(
		@ModelAttribute RequestProductApiSearchDTO request, Pageable pageable) {
		Page<ResponseProductsApiSearchDTO> products = productApiService.searchProducts(request, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(products);
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




}
