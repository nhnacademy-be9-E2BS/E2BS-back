package com.nhnacademy.back.cart.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForCustomerDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForGuestDTO;
import com.nhnacademy.back.cart.service.CartService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CartRestController {

	private final CartService cartService;

	/**
	 * 비회원/회원
	 */
	@PostMapping("/api/customers/carts/items")
	public ResponseEntity<Void> createCartItemForCustomer(@Validated @RequestBody RequestAddCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.createCartItemForCustomer(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/api/customers/carts/items/{cartItemId}")
	public ResponseEntity<Void> updateCartItemForCustomer(@PathVariable long cartItemId, @Validated @RequestBody RequestUpdateCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.updateCartItemForCustomer(cartItemId, requestDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/customers/carts/items/{cartItemId}")
	public ResponseEntity<Void> deleteCartItemForCustomer(@PathVariable Long cartItemId) {
		cartService.deleteCartItemForCustomer(cartItemId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/customers/{customerId}/carts")
	public ResponseEntity<Void> deleteCartForCustomer(@PathVariable long customerId) {
		cartService.deleteCartForCustomer(customerId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/api/customers/{customerId}/carts")
	public ResponseEntity<List<ResponseCartItemsForCustomerDTO>> getCartItemsByCustomer(@PathVariable long customerId) {
		List<ResponseCartItemsForCustomerDTO> body = cartService.getCartItemsByCustomer(customerId);
		return ResponseEntity.ok(body);
	}


	/**
	 * 게스트
	 */
	@PostMapping("/api/guests/carts/items")
	public ResponseEntity<Void> createCartItemForGuest(@Validated @RequestBody RequestAddCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.createCartItemForGuest(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/api/guests/carts/items")
	public ResponseEntity<Void> updateCartItemForGuest(@Validated @RequestBody RequestUpdateCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.updateCartItemForGuest(requestDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/guests/carts/items")
	public ResponseEntity<Void> deleteCartItemForGuest(@Validated @RequestBody RequestDeleteCartItemsForGuestDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.deleteCartItemForGuest(requestDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/guests/{sessionId}/carts")
	public ResponseEntity<Void> deleteCartForGuest(@PathVariable String sessionId) {
		cartService.deleteCartForGuest(sessionId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/api/guests/{sessionId}/carts")
	public ResponseEntity<List<ResponseCartItemsForGuestDTO>> getCartItemsByGuest(@PathVariable String sessionId) {
		List<ResponseCartItemsForGuestDTO> body = cartService.getCartItemsByGuest(sessionId);
		return ResponseEntity.ok(body);
	}
}
