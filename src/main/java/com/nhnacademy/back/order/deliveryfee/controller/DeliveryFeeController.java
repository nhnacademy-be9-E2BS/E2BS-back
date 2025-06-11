package com.nhnacademy.back.order.deliveryfee.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.order.deliveryfee.domain.dto.request.RequestDeliveryFeeDTO;
import com.nhnacademy.back.order.deliveryfee.domain.dto.response.ResponseDeliveryFeeDTO;
import com.nhnacademy.back.order.deliveryfee.service.DeliveryFeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "배송지 관련 API", description = "배송지 정책 관련 기능 제공")
@RequiredArgsConstructor
@RestController
public class DeliveryFeeController {
	private final DeliveryFeeService deliveryFeeService;

	/**
	 * 관리자의 배송비 정책 리스트 조회 기능
	 */
	@Operation(summary = "배송지 정책 조회", description = "관리자가 배송비 정책 목록을 조회할 수 있는 기능")
	@Admin
	@GetMapping("/api/auth/admin/deliveryFee")
	public ResponseEntity<Page<ResponseDeliveryFeeDTO>> getDeliveryFee(Pageable pageable) {
		return ResponseEntity.ok(deliveryFeeService.getDeliveryFees(pageable));
	}

	/**
	 * 관리자의 배송비 정책 추가 기능	
	 */
	@Operation(summary = "배송비 정책 추가", description = "관리자가 신규 배송비 정책을 추가할 수 있는 기능")
	@Admin
	@PostMapping("/api/auth/admin/deliveryFee")
	public ResponseEntity<Void> createDeliveryFee(@Validated @RequestBody RequestDeliveryFeeDTO deliveryFeeDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		deliveryFeeService.createDeliveryFee(deliveryFeeDTO);
		return ResponseEntity.ok().build();
	}

	/**
	 * 현재 적용 중(가장 최근에 추가된 정책)을 반환하는 메서드
	 */
	@Operation(summary = "현재 적용 중인 배송비 정책 조회", description = "사용자가 현재 적용 중인 배송비 정책을 확인할 수 있는 기능")
	@GetMapping("/api/deliveryFee")
	public ResponseEntity<ResponseDeliveryFeeDTO> getCurrentDeliveryFee() {
		return ResponseEntity.ok(deliveryFeeService.getCurrentDeliveryFee());
	}
}
