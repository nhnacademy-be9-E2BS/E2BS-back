package com.nhnacademy.back.product.state.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.service.ProductStateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productState")
public class ProductStateController {
	private final ProductStateService productStateService;

	/**
	 * ProductState 전체조회
	 */

	@GetMapping
	public ResponseEntity<List<ProductState>> getProductStates() {
		List<ProductState> productStates = productStateService.getProductStates();
		return ResponseEntity.status(HttpStatus.OK).body(productStates);
	}
}
