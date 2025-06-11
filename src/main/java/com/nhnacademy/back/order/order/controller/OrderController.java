package com.nhnacademy.back.order.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestPaymentApproveDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderWrapperDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponsePaymentConfirmDTO;
import com.nhnacademy.back.order.order.service.OrderService;
import com.nhnacademy.back.order.payment.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "공통 주문 API", description = "공통 주문 관련 기능 제공")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
	private final OrderService orderService;
	private final PaymentService paymentService;

	/**
	 * 프론트에서 요청한 주문서 정보를 저장
	 */
	@Operation(summary = "외부 API 결제 주문서 저장", description = "사용자가 외부 API 결제를 진행할 시 미리 DB에 주문 내역을 저장하는 기능")
	@PostMapping("/create/payment")
	public ResponseEntity<ResponseOrderResultDTO> createOrder(
		@Parameter(description = "주문 상품 정보") @Validated @RequestBody RequestOrderWrapperDTO request,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		return orderService.createOrder(request);
	}

	/**
	 * 프론트에서 온 결제 완료 응답으로 결제 승인 요청
	 * 포인트 차감, 쿠폰 사용 여부 변경, 포인트 적립 등을 추가로 호출해야 함
	 * 이는 이후 다른 부분 구현 완료 시 진행할 예정
	 * 외부 API에 대한 결제 이므로 결제 테이블에 저장도 요청해야 함
	 */
	@Operation(summary = "외부 API 결제 승인 요청", description = "사용자가 결제 완료 시 결제 승인을 요청하는 기능")
	@PostMapping("/confirm")
	public ResponseEntity<Void> orderConfirm(@RequestBody RequestPaymentApproveDTO approveRequest) {
		// 승인 하고 이후에 결과에 따른 롤백처리 필요 할 수 있음

		ResponseEntity<ResponsePaymentConfirmDTO> response = orderService.confirmOrder(approveRequest);
		if (response.getStatusCode().is2xxSuccessful()) {
			//결제 승인 완료 시 포인트 차감, 쿠폰 사용, 포인트 적립 호출, 결제 정보 저장
			paymentService.createPayment(response.getBody());
		} else { // 승인 실패 시 롤백
			orderService.deleteOrder(approveRequest.getOrderId());
		}
		return ResponseEntity.status(response.getStatusCode()).build();
	}

	/**
	 * 특정 주문서를 삭제하는 기능
	 */
	@Operation(summary = "외부 API 결제 모달 에러 시 주문서 삭제", description = "사용자가 결제 모달을 끌 시 주문서 내역을 삭제하는 기능")
	@PostMapping("/cancel")
	public ResponseEntity<Void> deleteOrder(@Parameter(description = "주문 코드") @RequestParam String orderId) {
		return orderService.deleteOrder(orderId);
	}

	/**
	 * 특정 주문 코드에 대한 주문 상세 정보를 반환하는 메서드
	 * 관리자 + 고객 둘 다 사용 메서드
	 */
	@Operation(summary = "특정 주문 내역 조회", description = "사용자가 특정 주문 코드의 주문 내역을 확인 가능하도록 반환하는 기능")
	@GetMapping("/{order-code}")
	public ResponseEntity<ResponseOrderWrapperDTO> getOrder(
		@Parameter(description = "주문 코드") @PathVariable(name = "order-code") String orderCode) {
		return ResponseEntity.ok(orderService.getOrderByOrderCode(orderCode));
	}

	/**
	 * 비회원용 주문 목록 조회
	 */
	@Operation(summary = "비회원 주문 내역 목록 조회", description = "비회원이 자신의 주문 내역을 확인하는 기능")
	@GetMapping("/customers/orders")
	ResponseEntity<Page<ResponseOrderDTO>> getOrdersByCustomerId(Pageable pageable,
		@Parameter(description = "비회원 식별 번호") @RequestParam long customerId) {
		return ResponseEntity.ok(orderService.getOrdersByCustomerId(pageable, customerId));
	}

}
