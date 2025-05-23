package com.nhnacademy.back.product.product.kim.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.kim.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class ProductController {

	private final ProductService productService;

	/**
	 * 카테고리별 도서 여러권 페이징 처리하여 조회
	 */
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<Page<ResponseProductReadDTO>> getProducts(
		@PageableDefault(page = 0, size = 10) Pageable pageable, @PathVariable long categoryId) {
		Page<ResponseProductReadDTO> response = productService.getProducts(pageable, categoryId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 도서 단일 조회
	 */
	@GetMapping("/{bookId}")
	public ResponseEntity<ResponseProductReadDTO> getProduct(@PathVariable long bookId) {
		ResponseProductReadDTO response = productService.getProduct(bookId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
