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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

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
	 * 공통 처리
	 */
	@GetMapping("/api/carts/counts")
	public ResponseEntity<Integer> getCartItemsCounts(@RequestParam String memberId, @RequestParam String sessionId) {
		Integer result;

		if (StringUtils.isEmpty(memberId)) {
			result = cartService.getCartItemsCountsForGuest(sessionId);
		} else {
			result = cartService.getCartItemsCountsForMember(memberId);
		}

		return ResponseEntity.ok(result);
	}

	/**
	 * 회원
	 */
	@PostMapping("/api/auth/members/carts/items")
	public ResponseEntity<Integer> createCartItemForMember(@Validated @RequestBody RequestAddCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors() || (Objects.isNull(requestDto.getMemberId()) && Objects.isNull(requestDto.getSessionId()))) {
			throw new ValidationFailedException(bindingResult);
		}

		int cartQuantity = cartService.createCartItemForMember(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(cartQuantity);
	}

	@PutMapping("/api/auth/members/carts/items/{cartItemId}")
	public ResponseEntity<Integer> updateCartItemForMember(@PathVariable long cartItemId, @Validated @RequestBody RequestUpdateCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		int cartQuantity = cartService.updateCartItemForMember(cartItemId, requestDto);
		return ResponseEntity.ok(cartQuantity);
	}

	@DeleteMapping("/api/auth/members/carts/items/{cartItemId}")
	public ResponseEntity<Void> deleteCartItemForMember(@PathVariable long cartItemId) {
		cartService.deleteCartItemForMember(cartItemId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/api/auth/members/{memberId}/carts")
	public ResponseEntity<Void> deleteCartForMember(@PathVariable String memberId) {
		cartService.deleteCartForMember(memberId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/api/auth/members/{memberId}/carts")
	public ResponseEntity<List<ResponseCartItemsForMemberDTO>> getCartItemsByMember(@PathVariable String memberId) {
		List<ResponseCartItemsForMemberDTO> body = cartService.getCartItemsByMember(memberId);
		return ResponseEntity.ok(body);
	}


	/**
	 * 게스트
	 */
	@PostMapping("/api/guests/carts/items")
	public ResponseEntity<Integer> createCartItemForGuest(@Validated @RequestBody RequestAddCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		int cartQuantity = cartService.createCartItemForGuest(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(cartQuantity);
	}

	@PutMapping("/api/guests/carts/items")
	public ResponseEntity<Integer> updateCartItemForGuest(@Validated @RequestBody RequestUpdateCartItemsDTO requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		int cartQuantity = cartService.updateCartItemForGuest(requestDto);
		return ResponseEntity.ok(cartQuantity);
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
