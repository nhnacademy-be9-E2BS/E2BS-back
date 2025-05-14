package com.nhnacademy.back.order.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;
import com.nhnacademy.back.order.deliveryfee.domain.entity.DeliveryFee;
import com.nhnacademy.back.order.deliveryfee.repository.DeliveryFeeJpaRepository;
import com.nhnacademy.back.order.order.adaptor.TossConfirmAdaptor;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderDetailDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestTossConfirmDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseTossPaymentConfirmDTO;
import com.nhnacademy.back.order.order.domain.entity.Order;
import com.nhnacademy.back.order.order.domain.entity.OrderDetail;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.OrderService;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;
import com.nhnacademy.back.order.orderstate.repository.OrderStateJpaRepository;
import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;
import com.nhnacademy.back.order.wrapper.exception.WrapperNotFoundException;
import com.nhnacademy.back.order.wrapper.repository.WrapperJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

	@Value("${order.sc}")
	private String secretKey;

	private final OrderJpaRepository orderJpaRepository;
	private final OrderDetailJpaRepository orderDetailJpaRepository;
	private final MemberCouponJpaRepository memberCouponJpaRepository;
	private final DeliveryFeeJpaRepository deliveryFeeJpaRepository;
	private final CustomerJpaRepository customerJpaRepository;

	private final ProductJpaRepository productJpaRepository;
	private final OrderStateJpaRepository orderStateJpaRepository;
	private final WrapperJpaRepository wrapperJpaRepository;

	private final TossConfirmAdaptor tossConfirmAdaptor;

	/**
	 * 주문서를 저장하는 서비스
	 * 재고 차감은 이후 추후 추가 예정
	 * null이면 안되는 외래키의 경우 orElseThrow로 예외 발생 예정
	 * 추후 해당 도메인 담당자가 Exception을 만들면 추가할 예정
	 */
	@Transactional
	@Override
	public ResponseEntity<ResponseOrderResultDTO> createOrder(RequestOrderWrapperDTO requestOrderWrapperDTO) {
		// 주문서 저장
		RequestOrderDTO requestOrderDTO = requestOrderWrapperDTO.getOrder();
		Customer customer = customerJpaRepository.findById(requestOrderDTO.getCustomerId()).orElseThrow();
		MemberCoupon memberCoupon = null;
		// 사용한 쿠폰이 있는 경우에만 Repository 검색
		if (requestOrderDTO.getMemberCouponId() != null) {
			memberCoupon = memberCouponJpaRepository.findById(requestOrderDTO.getMemberCouponId()).orElseThrow();
		}
		DeliveryFee deliveryFee = deliveryFeeJpaRepository.findById(requestOrderDTO.getDeliveryFeeId()).orElseThrow();

		Order order = new Order(requestOrderDTO, memberCoupon, deliveryFee, customer);
		orderJpaRepository.save(order);

		// 주문 상세 저장
		String orderCode = order.getOrderCode();
		List<RequestOrderDetailDTO> requestOrderDetailDTOs = requestOrderWrapperDTO.getOrderDetails();

		// 실제 주문서 ID와 맞추기
		requestOrderDetailDTOs.forEach(orderDetailDTO -> {
			orderDetailDTO.setOrderCode(orderCode);
		});

		for (RequestOrderDetailDTO requestOrderDetailDTO : requestOrderDetailDTOs) {
			Product product = productJpaRepository.findById(requestOrderDetailDTO.getProductId()).orElseThrow();
			OrderState orderState = orderStateJpaRepository.findById(requestOrderDetailDTO.getOrderStateId())
				.orElseThrow();
			Wrapper wrapper = wrapperJpaRepository.findById(requestOrderDetailDTO.getWrapperId())
				.orElseThrow(() -> new WrapperNotFoundException(
					"wrapper not found id: " + requestOrderDetailDTO.getWrapperId()));
			OrderDetail orderDetail = new OrderDetail(requestOrderDetailDTO, product, orderState, order, wrapper);

			orderDetailJpaRepository.save(orderDetail);
		}

		return ResponseEntity.ok(new ResponseOrderResultDTO(orderCode, order.getOrderPaymentAmount()));
	}

	/**
	 * 결제 완료 시 토스에 결제 승인 요청을 보내는 서비스
	 */
	@Override
	public ResponseEntity<ResponseTossPaymentConfirmDTO> confirmOrder(String orderId, String paymentKey, long amount) {
		RequestTossConfirmDTO requestTossConfirmDTO = new RequestTossConfirmDTO(orderId, paymentKey, amount);
		// 결제 승인 결과를 받아 온 응답
		ResponseEntity<ResponseTossPaymentConfirmDTO> response = tossConfirmAdaptor.confirmOrder(requestTossConfirmDTO,
			secretKey);
		// 만약 승인된 경우 결제 상태 업데이트
		if (response.getStatusCode().is2xxSuccessful()) {
			Order order = orderJpaRepository.findById(orderId).orElseThrow();
			order.updatePaymentStatus(true);
			orderJpaRepository.save(order);
		}
		return response;
	}

	// 요소 제거 시 트랜잭션 없을 시 에러 발생
	// 재고 복구 추가해야 함
	@Transactional
	@Override
	public ResponseEntity<Void> cancelOrder(String orderId) {
		orderDetailJpaRepository.deleteByOrderOrderCode(orderId);
		orderJpaRepository.deleteById(orderId);
		return ResponseEntity.ok().build();
	}

}
