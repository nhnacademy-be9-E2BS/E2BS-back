package com.nhnacademy.back.order.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.service.OrderAdminService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class OrderAdminController {
	private final OrderAdminService orderAdminService;

	@GetMapping("/api/admin/orders")
	public ResponseEntity<Page<ResponseOrderDTO>> getOrders(@PageableDefault(page = 0, size = 10) Pageable pageable) {
		return ResponseEntity.ok(orderAdminService.getOrders(pageable));
	}
}
