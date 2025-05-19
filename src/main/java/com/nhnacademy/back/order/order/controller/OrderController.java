package com.nhnacademy.back.order.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Member;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseTossPaymentConfirmDTO;
import com.nhnacademy.back.order.order.service.OrderService;
import com.nhnacademy.back.order.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class OrderController {

	private final OrderService orderService;
	private final PaymentService paymentService;

	/**
	 * 프론트에서 요청한 주문서 정보를 저장
	 */
	@Member
	@PostMapping("/api/order/create/tossPay")
	public ResponseEntity<ResponseOrderResultDTO> createOrder(@Validated @RequestBody RequestOrderWrapperDTO request,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		return orderService.createOrder(request);
	}

	/**
	 * 포인트 주문에 대한 처리
	 */
	@Member
	@PostMapping("/api/order/create/point")
	public ResponseEntity<ResponseOrderResultDTO> createPointOrder(
		@Validated @RequestBody RequestOrderWrapperDTO request,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		return orderService.createPointOrder(request);
	}

	/**
	 * 프론트에서 온 결제 완료 응답으로 결제 승인 요청
	 * 포인트 차감, 쿠폰 사용 여부 변경, 포인트 적립 등을 추가로 호출해야 함
	 * 이는 이후 다른 부분 구현 완료 시 진행할 예정
	 * 외부 API에 대한 결제 이므로 결제 테이블에 저장도 요청해야 함
	 */
	@Member
	@PostMapping("/api/order/confirm")
	public ResponseEntity<Void> orderConfirm(@RequestParam String orderId, @RequestParam String paymentKey,
		@RequestParam long amount) {
		// 승인 하고 이후에 결과에 따른 롤백처리 필요 할 수 있음
		ResponseEntity<ResponseTossPaymentConfirmDTO> response = orderService.confirmOrder(orderId, paymentKey, amount);
		if (response.getStatusCode().is2xxSuccessful()) {
			//결제 승인 완료 시 포인트 차감, 쿠폰 사용, 포인트 적립 호출, 결제 정보 저장
			paymentService.createPayment(response.getBody());
		} else { // 승인 실패 시 롤백
			cancelOrder(orderId);
		}
		return ResponseEntity.status(response.getStatusCode()).build();
	}

	/**
	 * 특정 주문서를 삭제하는 기능, 안에서 재고 복구도 진행함
	 */
	@Member
	@PostMapping("/api/order/cancel")
	public ResponseEntity<Void> cancelOrder(@RequestParam String orderId) {
		return orderService.cancelOrder(orderId);
	}

	/**
	 * 특정 주문 코드에 대한 주문 상세 정보를 반환하는 메서드
	 * 관리자 + 고객 둘 다 사용 메서드
	 */
	@GetMapping("/api/order/{orderCode}")
	public ResponseEntity<ResponseOrderWrapperDTO> getOrder(@PathVariable String orderCode) {
		return ResponseEntity.ok(orderService.getOrderByOrderCode(orderCode));
	}
}
