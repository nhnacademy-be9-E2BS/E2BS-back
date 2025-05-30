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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class DeliveryFeeController {
	private final DeliveryFeeService deliveryFeeService;

	/**
	 * 관리자의 배송비 정책 리스트 조회 기능
	 */
	@Admin
	@GetMapping("/api/admin/deliveryFee")
	public ResponseEntity<Page<ResponseDeliveryFeeDTO>> getDeliveryFee(Pageable pageable) {
		return ResponseEntity.ok(deliveryFeeService.getDeliveryFees(pageable));
	}

	/**
	 * 관리자의 배송비 정책 추가 기능	
	 */
	@Admin
	@PostMapping("/api/admin/deliveryFee")
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
	@GetMapping("/api/deliveryFee")
	public ResponseEntity<ResponseDeliveryFeeDTO> getCurrentDeliveryFee() {
		return ResponseEntity.ok(deliveryFeeService.getCurrentDeliveryFee());
	}
}
