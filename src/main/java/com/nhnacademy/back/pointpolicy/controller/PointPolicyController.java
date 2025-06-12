package com.nhnacademy.back.pointpolicy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.pointpolicy.domain.dto.request.RequestPointPolicyRegisterDTO;
import com.nhnacademy.back.pointpolicy.domain.dto.request.RequestPointPolicyUpdateDTO;
import com.nhnacademy.back.pointpolicy.domain.dto.response.ResponsePointPolicyDTO;
import com.nhnacademy.back.pointpolicy.service.PointPolicyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "포인트 정책 관리", description = "포인트 정책 생성, 조회, 수정, 활성화 API")
public class PointPolicyController {

	private final PointPolicyService pointPolicyService;

	@Operation(summary = "포인트 정책 등록", description = "새로운 포인트 정책을 생성")
	@ApiResponse(responseCode = "201", description = "포인트 정책 생성 성공")
	@Admin
	@PostMapping("/api/admin/pointPolicies/register")
	public ResponseEntity<Void> createPointPolicy(
		@RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "포인트 정책 생성 요청 DTO",
			required = true,
			content = @Content(schema = @Schema(implementation = RequestPointPolicyRegisterDTO.class))
		) RequestPointPolicyRegisterDTO request) {

		pointPolicyService.createPointPolicy(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "회원가입 포인트 정책 조회")
	@ApiResponse(responseCode = "200", description = "회원가입 포인트 정책 조회 성공")
	@Admin
	@GetMapping("/api/admin/pointPolicies/registerPolicy")
	public ResponseEntity<List<ResponsePointPolicyDTO>> getRegisterPointPolicies() {
		return ResponseEntity.ok(pointPolicyService.getRegisterPointPolicies());
	}

	@Operation(summary = "이미지 리뷰 포인트 정책 조회")
	@ApiResponse(responseCode = "200", description = "이미지 리뷰 포인트 정책 조회 성공")
	@Admin
	@GetMapping("/api/admin/pointPolicies/reviewImgPolicy")
	public ResponseEntity<List<ResponsePointPolicyDTO>> getReviewImgPointPolicies() {
		return ResponseEntity.ok(pointPolicyService.getReviewImgPointPolicies());
	}

	@Operation(summary = "일반 리뷰 포인트 정책 조회")
	@ApiResponse(responseCode = "200", description = "일반 리뷰 포인트 정책 조회 성공")
	@Admin
	@GetMapping("/api/admin/pointPolicies/reviewPolicy")
	public ResponseEntity<List<ResponsePointPolicyDTO>> getReviewPointPolicies() {
		return ResponseEntity.ok(pointPolicyService.getReviewPointPolicies());
	}

	@Operation(summary = "기본 적립률 포인트 정책 조회")
	@ApiResponse(responseCode = "200", description = "기본 적립률 포인트 정책 조회 성공")
	@Admin
	@GetMapping("/api/admin/pointPolicies/bookPolicy")
	public ResponseEntity<List<ResponsePointPolicyDTO>> getBookPointPolicies() {
		return ResponseEntity.ok(pointPolicyService.getBookPointPolicies());
	}

	@Operation(summary = "포인트 정책 활성화", description = "특정 포인트 정책 활성화")
	@ApiResponse(responseCode = "204", description = "활성화 성공")
	@ApiResponse(responseCode = "404", description = "정책 ID 없음")
	@Admin
	@PutMapping("/api/admin/pointPolicies/{point-policy-id}/activate")
	public ResponseEntity<Void> activatePointPolicy(
		@Parameter(description = "활성화할 포인트 정책 ID", example = "1") @PathVariable("point-policy-id") Long pointPolicyId) {

		pointPolicyService.activatePointPolicy(pointPolicyId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "포인트 정책 수정", description = "기존 포인트 정책 내용을 수정")
	@ApiResponse(responseCode = "204", description = "수정 성공")
	@ApiResponse(responseCode = "400", description = "요청값 오류")
	@ApiResponse(responseCode = "404", description = "정책 ID 없음")
	@Admin
	@PutMapping("/api/admin/pointPolicies/{point-policy-id}")
	public ResponseEntity<Void> updatePointPolicy(
		@Parameter(description = "수정할 포인트 정책 ID", example = "1") @PathVariable("point-policy-id") Long pointPolicyId,
		@RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "포인트 정책 수정 요청 DTO",
			required = true,
			content = @Content(schema = @Schema(implementation = RequestPointPolicyUpdateDTO.class))
		) RequestPointPolicyUpdateDTO request) {

		pointPolicyService.updatePointPolicy(pointPolicyId, request);
		return ResponseEntity.noContent().build();
	}
}
