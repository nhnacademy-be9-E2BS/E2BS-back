package com.nhnacademy.back.product.like.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.product.like.domain.dto.request.RequestCreateLikeDTO;
import com.nhnacademy.back.product.like.domain.dto.response.ResponseLikedProductDTO;
import com.nhnacademy.back.product.like.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LikeRestController {

	private final LikeService likeService;


	@PostMapping("/api/products/{productId}/likes")
	public ResponseEntity<Void> createLike(@PathVariable long productId, @Validated @RequestBody RequestCreateLikeDTO likeDTO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}

		likeService.createLike(productId, likeDTO.getCustomerId());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/api/products/{productId}/likes")
	public ResponseEntity<Void> deleteLike(@PathVariable long productId, @RequestParam long customerId) {
		likeService.deleteLike(productId, customerId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/api/products/likes")
	public ResponseEntity<Page<ResponseLikedProductDTO>> getLikedProductsByCustomer(@RequestParam long customerId, Pageable pageable) {
		Page<ResponseLikedProductDTO> body = likeService.getLikedProductsByCustomer(customerId, pageable);
		return ResponseEntity.ok(body);
	}

	@GetMapping("/api/products/{productId}/likes/counts")
	public ResponseEntity<Long> getLikeCounts(@PathVariable long productId) {
		long body = likeService.getLikeCount(productId);
		return ResponseEntity.ok(body);
	}

}
