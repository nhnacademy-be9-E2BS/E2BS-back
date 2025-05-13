package com.nhnacademy.back.cart.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.cart.domain.dto.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsForCustomerDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsForGuestDTO;
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
	public ResponseEntity<?> createCartItemForCustomer(@Validated @RequestBody RequestAddCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.createCartItemForCustomer(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/api/customers/carts/items/{cartItemId}")
	public ResponseEntity<?> updateCartItemForCustomer(@PathVariable Long cartItemId, @Validated @RequestBody RequestUpdateCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.updateCartItemForCustomer(cartItemId, requestDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/customers/carts/items/{cartItemId}")
	public ResponseEntity<?> deleteCartItemForCustomer(@PathVariable Long cartItemId) {
		cartService.deleteCartItemForCustomer(cartItemId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/customers/{customerId}/carts")
	public ResponseEntity<?> deleteCartForCustomer(@PathVariable long customerId) {
		cartService.deleteCartForCustomer(customerId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/api/customers/{customerId}/carts")
	public ResponseEntity<Page<ResponseCartItemsForCustomerDTO>> getCartItemsByCustomer(@PathVariable Long customerId, @PageableDefault(size = 5) Pageable pageable) {
		Page<ResponseCartItemsForCustomerDTO> body = cartService.getCartItemsByCustomer(customerId, pageable);
		return ResponseEntity.ok(body);
	}


	/**
	 * 게스트
	 */
	@PostMapping("/api/guests/carts/items")
	public ResponseEntity<?> createCartItemForGuest(@Validated @RequestBody RequestAddCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.createCartItemForGuest(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/api/guests/carts/items")
	public ResponseEntity<?> updateCartItemForGuest(@Validated @RequestBody RequestUpdateCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.updateCartItemForGuest(requestDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/guests/carts/items")
	public ResponseEntity<?> deleteCartItemForGuest(@Validated @RequestBody RequestDeleteCartItemsForGuestDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.deleteCartItemForGuest(requestDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/guests/{sessionId}/carts")
	public ResponseEntity<?> deleteCartForGuest(@PathVariable String sessionId) {
		cartService.deleteCartForGuest(sessionId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/api/guests/{sessionId}/carts")
	public ResponseEntity<Page<ResponseCartItemsForGuestDTO>> getCartItemsByGuest(@PathVariable String sessionId, @PageableDefault(size = 5) Pageable pageable) {
		Page<ResponseCartItemsForGuestDTO> body = cartService.getCartItemsByGuest(sessionId, pageable);
		return ResponseEntity.ok(body);
	}
}
