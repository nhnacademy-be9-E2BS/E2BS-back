package com.nhnacademy.back.product.tag.controller;

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

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.product.tag.domain.dto.request.RequestTagDTO;
import com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO;
import com.nhnacademy.back.product.tag.service.TagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/tags")
public class TagController {
	private final TagService tagService;

	/**
	 * Tag 전체 조회
	 * 200 상태코드와 태그를 리스트 타입으로 반환
	 */
	@Admin
	@GetMapping
	public ResponseEntity<Page<ResponseTagDTO>> getTags(@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponseTagDTO> tags = tagService.getTags(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(tags);
	}

	/**
	 * RequestTagDTO를 받아 DB에 생성하여 저장
	 * 201 상태코드 반환
	 */
	@Admin
	@PostMapping
	public ResponseEntity<Void> createTag(@RequestBody RequestTagDTO request) {
		tagService.createTag(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * tagId와 RequestTagDTO를 받아 DB에 기존 정보를 업데이트
	 * 200 상태코드 반환
	 */
	@Admin
	@PutMapping("/{tagId}")
	public ResponseEntity<Void> updateTag(@PathVariable Long tagId, @RequestBody RequestTagDTO request) {
		tagService.updateTag(tagId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * tagName을 받아 DB에서 태그 삭제
	 * 200 상태코드 반환
	 */
	@Admin
	@DeleteMapping("/{tagId}")
	public ResponseEntity<Void> deleteTag(@PathVariable Long tagId, @RequestBody RequestTagDTO request) {
		tagService.deleteTag(tagId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}



}
