package com.nhnacademy.back.cart.controller;

import java.util.List;
import java.util.Objects;

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
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForMemberDTO;
import com.nhnacademy.back.cart.service.CartService;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CartRestController {

	private final CartService cartService;

	/**
	 * 회원
	 */
	@PostMapping("/api/members/carts/items")
	public ResponseEntity<Void> createCartItemForMember(@Validated @RequestBody RequestAddCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors() || (Objects.isNull(requestDto.getMemberId()) && Objects.isNull(requestDto.getSessionId()))) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.createCartItemForMember(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/api/members/carts/items/{cartItemId}")
	public ResponseEntity<Void> updateCartItemForMember(@PathVariable long cartItemId, @Validated @RequestBody RequestUpdateCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		cartService.updateCartItemForMember(cartItemId, requestDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/members/carts/items/{cartItemId}")
	public ResponseEntity<Void> deleteCartItemForMember(@PathVariable long cartItemId) {
		cartService.deleteCartItemForMember(cartItemId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/members/{memberId}/carts")
	public ResponseEntity<Void> deleteCartForMember(@PathVariable String memberId) {
		cartService.deleteCartForMember(memberId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/api/members/{memberId}/carts")
	public ResponseEntity<List<ResponseCartItemsForMemberDTO>> getCartItemsByMember(@PathVariable String memberId) {
		List<ResponseCartItemsForMemberDTO> body = cartService.getCartItemsByMember(memberId);
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
