package com.nhnacademy.back.product.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.product.product.domain.dto.request.RequestMainPageProductDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;
import com.nhnacademy.back.product.product.service.MainPageProductService;

@RestController
public class MainPageProductController {
	private final MainPageProductService mainPageProductService;

	public MainPageProductController(MainPageProductService mainPageProductService) {
		this.mainPageProductService = mainPageProductService;
	}

	@GetMapping()
	public ResponseEntity<Page<ResponseMainPageProductDTO>> getProductsByCategory(
		@ModelAttribute RequestMainPageProductDTO request, Pageable pageable) {
		Page<ResponseMainPageProductDTO> products = mainPageProductService.showProducts(request, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(products);
	}
}
