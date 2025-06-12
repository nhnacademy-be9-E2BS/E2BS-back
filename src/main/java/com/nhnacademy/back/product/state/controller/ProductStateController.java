package com.nhnacademy.back.product.state.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.service.ProductStateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
@Tag(name = "도서 상태", description = "도서 상태 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productState")
public class ProductStateController {
	private final ProductStateService productStateService;

	/**
	 * ProductState 전체조회
	 */
	@Operation(summary = "모든 도서 상태 리스트 조회",
		description = "관리자 페이지에서 모든 도서 상태 리스트를 조회합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공")
		})
	@GetMapping
	public ResponseEntity<List<ProductState>> getProductStates() {
		List<ProductState> productStates = productStateService.getProductStates();
		return ResponseEntity.status(HttpStatus.OK).body(productStates);
	}
}
