package com.nhnacademy.back.product.contributor.controller;

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

import com.nhnacademy.back.product.contributor.domain.dto.request.RequestPositionDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponsePositionDTO;

import com.nhnacademy.back.product.contributor.service.PositionService;

@RestController
@RequestMapping("/api/admin/positions")
public class PositionController {
	private final PositionService positionService;
	public PositionController(PositionService positionService) {
		this.positionService = positionService;
	}

	/**
	 * position 전체 조회
	 */
	@GetMapping()
	public ResponseEntity<Page<ResponsePositionDTO>> getPositions(@PageableDefault() Pageable pageable) {
		Page<ResponsePositionDTO> responsePositionDTOs = positionService.getPositions(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(responsePositionDTOs);
	}

	/**
	 * position 단건 조회
	 */
	@GetMapping("/{positionId}")
	public ResponseEntity<ResponsePositionDTO> getPosition(@PathVariable Long positionId) {
		ResponsePositionDTO responsePositionDTO = positionService.getPosition(positionId);
		return ResponseEntity.status(HttpStatus.OK).body(responsePositionDTO);
	}

	/**
	 * position 생성
	 */
	@PostMapping()
	public ResponseEntity<?> createPosition(@RequestBody RequestPositionDTO request) {
		positionService.createPosition(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	/**
	 * position 수정
	 */
	@PutMapping("/{positionId}")
	public ResponseEntity<?> updatePosition(@PathVariable Long positionId, @RequestBody RequestPositionDTO request) {
		ResponsePositionDTO responsePositionDTO = positionService.updatePosition(positionId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(responsePositionDTO);
	}

}
