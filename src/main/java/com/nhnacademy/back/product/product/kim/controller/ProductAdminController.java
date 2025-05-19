package com.nhnacademy.back.product.product.kim.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.kim.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
public class ProductAdminController {
	private final ProductService productService;

	@Admin
	@PostMapping
	public ResponseEntity<Void> createProduct(@RequestBody Product product) {

	}
}
