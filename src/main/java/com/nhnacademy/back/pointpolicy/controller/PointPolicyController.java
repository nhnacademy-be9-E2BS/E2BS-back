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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PointPolicyController {

	private final PointPolicyService pointPolicyService;

	/**
	 * 포인트 정책 생성
	 */
	@Admin
	@PostMapping("/api/admin/point-policies/register")
	public ResponseEntity<Void> createPointPolicy(@RequestBody RequestPointPolicyRegisterDTO request) {
		pointPolicyService.createPointPolicy(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 회원가입 포인트 정책 조회
	 */
	@Admin
	@GetMapping("/api/admin/point-policies/register-policy")
	public ResponseEntity<List<ResponsePointPolicyDTO>> getRegisterPointPolicies() {
		List<ResponsePointPolicyDTO> response = pointPolicyService.getRegisterPointPolicies();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 이미지 리뷰 포인트 정책 조회
	 */
	@Admin
	@GetMapping("/api/admin/point-policies/review-img-policy")
	public ResponseEntity<List<ResponsePointPolicyDTO>> getReviewImgPointPolicies() {
		List<ResponsePointPolicyDTO> response = pointPolicyService.getReviewImgPointPolicies();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 일반 리뷰 포인트 정책 조회
	 */
	@Admin
	@GetMapping("/api/admin/point-policies/review-policy")
	public ResponseEntity<List<ResponsePointPolicyDTO>> getReviewPointPolicies() {
		List<ResponsePointPolicyDTO> response = pointPolicyService.getReviewPointPolicies();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 기본 적립률 정책 조회
	 */
	@Admin
	@GetMapping("/api/admin/point-policies/book-policy")
	public ResponseEntity<List<ResponsePointPolicyDTO>> getBookPointPolicies() {
		List<ResponsePointPolicyDTO> response = pointPolicyService.getBookPointPolicies();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 포인트 정책 활성화
	 */
	@Admin
	@PutMapping("/api/admin/point-policies/{point-policyId}/activate")
	public ResponseEntity<Void> activatePointPolicy(@PathVariable("point-policyId") Long pointPolicyId) {
		pointPolicyService.activatePointPolicy(pointPolicyId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Admin
	@PutMapping("/api/admin/point-policies/{point-policyId}")
	public ResponseEntity<Void> updatePointPolicy(@PathVariable("point-policyId") Long pointPolicyId, @RequestBody RequestPointPolicyUpdateDTO request) {
		pointPolicyService.updatePointPolicy(pointPolicyId, request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
