package com.nhnacademy.back.product.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "도서(사용자)", description = "사용자 도서 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class ProductController {

	private final ProductService productService;

	/**
	 * 도서 단일 조회
	 */
	@Operation(summary = "도서 단일 조회",
		description = "도서 상세페이지를 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@GetMapping("/{book-id}")
	public ResponseEntity<ResponseProductReadDTO> getProduct(
		@Parameter(description = "조회할 도서 ID", example = "1", required = true) @PathVariable("book-id") long bookId) {
		ResponseProductReadDTO response = productService.getProduct(bookId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * order전용 productId들을 받아서 정보를 반환
	 * 200 상태코드 반환
	 */
	@Operation(summary = "도서 ID 리스트로 각 상품 정보 조회",
		description = "주문 페이지에서 각 도서의 정보를 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@GetMapping("/order")
	public ResponseEntity<List<ResponseProductReadDTO>> getProducts(
		@Parameter(description = "도서 ID 리스트", required = true) @RequestParam("products") List<Long> products) {
		List<ResponseProductReadDTO> productsDTO = productService.getProducts(products);
		return ResponseEntity.status(HttpStatus.OK).body(productsDTO);
	}
}
