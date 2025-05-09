package com.nhnacademy.back.cart.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.cart.domain.dto.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsDTO;
import com.nhnacademy.back.cart.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CartRestController {

	private final CartService cartService;

	@PostMapping("/api/carts/items")
	public ResponseEntity<?> createCartItem(@RequestBody RequestAddCartItemsDTO requestDto) {
		cartService.createCartItem(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/api/carts/items/{cartItemId}")
	public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId, @RequestBody RequestUpdateCartItemsDTO requestDto) {
		cartService.updateCartItem(cartItemId, requestDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/carts/items/{cartItemId}")
	public ResponseEntity<?> deleteCartItem(@PathVariable Long cartItemId) {
		cartService.deleteCartItem(cartItemId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/api/customers/{customerId}/carts")
	public ResponseEntity<Page<ResponseCartItemsDTO>> getCartItemsByCustomer(@PathVariable Long customerId, @PageableDefault(size = 5) Pageable pageable) {
		Page<ResponseCartItemsDTO> body = cartService.getCartItemsByCustomer(customerId, pageable);
		return ResponseEntity.ok(body);
	}

}
