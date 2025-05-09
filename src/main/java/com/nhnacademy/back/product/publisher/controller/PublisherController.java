package com.nhnacademy.back.product.publisher.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;
import com.nhnacademy.back.product.publisher.service.PublisherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/publishers")
public class PublisherController {
	private final PublisherService publisherService;

	/**
	 * Publisher 리스트 조회
	 */
	@GetMapping
	public ResponseEntity<Page<ResponsePublisherDTO>> getPublishers(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponsePublisherDTO> response = publisherService.getPublishers(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Publisher 저장
	 */
	@PostMapping
	public ResponseEntity<?> createPublisher(@RequestBody RequestPublisherDTO request) {
		publisherService.createPublisher(request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * Publisher 수정
	 */
	@PutMapping("/{publisherId}")
	public ResponseEntity<?> updatePublisher(@PathVariable Long publisherId, @RequestBody RequestPublisherDTO request) {
		publisherService.updatePublisher(publisherId, request);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
