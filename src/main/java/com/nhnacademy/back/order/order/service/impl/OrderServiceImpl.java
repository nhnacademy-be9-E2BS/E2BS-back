package com.nhnacademy.back.order.order.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;
import com.nhnacademy.back.order.deliveryfee.domain.entity.DeliveryFee;
import com.nhnacademy.back.order.deliveryfee.repository.DeliveryFeeJpaRepository;
import com.nhnacademy.back.order.order.adaptor.TossConfirmAdaptor;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderDetailDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestTossConfirmDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderDetailDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseTossPaymentConfirmDTO;
import com.nhnacademy.back.order.order.domain.entity.Order;
import com.nhnacademy.back.order.order.domain.entity.OrderDetail;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.OrderService;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderStateName;
import com.nhnacademy.back.order.orderstate.repository.OrderStateJpaRepository;
import com.nhnacademy.back.order.payment.domain.entity.Payment;
import com.nhnacademy.back.order.payment.repository.PaymentJpaRepository;
import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;
import com.nhnacademy.back.order.wrapper.exception.WrapperNotFoundException;
import com.nhnacademy.back.order.wrapper.repository.WrapperJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

	private final MemberJpaRepository memberJpaRepository;
	@Value("${order.sc}")
	private String secretKey;

	private final OrderJpaRepository orderJpaRepository;
	private final OrderDetailJpaRepository orderDetailJpaRepository;
	private final MemberCouponJpaRepository memberCouponJpaRepository;
	private final DeliveryFeeJpaRepository deliveryFeeJpaRepository;
	private final CustomerJpaRepository customerJpaRepository;

	private final PaymentJpaRepository paymentJpaRepository;

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
		return saveOrder(requestOrderWrapperDTO);
	}

	/**
	 * 포인트 주문 시 주문서 저장 및 결제 차감을 진행함
	 *
	 */
	@Transactional
	@Override
	public ResponseEntity<ResponseOrderResultDTO> createPointOrder(RequestOrderWrapperDTO requestOrderWrapperDTO) {
		ResponseEntity<ResponseOrderResultDTO> response = saveOrder(requestOrderWrapperDTO);
		//포인트 차감,적립 요청, 쿠폰 사용 요청, 결제 여부 최신화

		Order order = orderJpaRepository.findById(response.getBody().getOrderId()).orElseThrow();
		order.updatePaymentStatus(true);
		return response;
	}

	/*
	 * 주문서를 저장하고 저장 결과를 반환하는 메서드
	 * 트랜잭션 내에서 재사용을 위해 분리함
	 */
	private ResponseEntity<ResponseOrderResultDTO> saveOrder(RequestOrderWrapperDTO requestOrderWrapperDTO) {
		// 주문서 저장
		RequestOrderDTO requestOrderDTO = requestOrderWrapperDTO.getOrder();
		Customer customer = customerJpaRepository.findById(requestOrderDTO.getCustomerId()).orElseThrow();
		MemberCoupon memberCoupon = null;
		// 사용한 쿠폰이 있는 경우에만 Repository 검색, 사용 가능한 쿠폰이 맞는지도 봐야함
		if (requestOrderDTO.getMemberCouponId() != null) {
			memberCoupon = memberCouponJpaRepository.findById(requestOrderDTO.getMemberCouponId()).orElseThrow();
		}
		DeliveryFee deliveryFee = deliveryFeeJpaRepository.findById(requestOrderDTO.getDeliveryFeeId()).orElseThrow();
		OrderState orderState = orderStateJpaRepository.findByOrderStateName(OrderStateName.WAIT).orElseThrow();

		Order order = new Order(requestOrderDTO, memberCoupon, deliveryFee, customer, orderState);
		orderJpaRepository.save(order);

		// 주문 상세 저장
		String orderCode = order.getOrderCode();
		List<RequestOrderDetailDTO> requestOrderDetailDTOs = requestOrderWrapperDTO.getOrderDetails();

		// 실제 주문서 ID와 맞추기
		requestOrderDetailDTOs.forEach(orderDetailDTO -> orderDetailDTO.setOrderCode(orderCode));

		for (RequestOrderDetailDTO requestOrderDetailDTO : requestOrderDetailDTOs) {
			Product product = productJpaRepository.findById(requestOrderDetailDTO.getProductId()).orElseThrow();
			Wrapper wrapper = wrapperJpaRepository.findById(requestOrderDetailDTO.getWrapperId())
				.orElseThrow(() -> new WrapperNotFoundException(
					"wrapper not found id: " + requestOrderDetailDTO.getWrapperId()));
			OrderDetail orderDetail = new OrderDetail(requestOrderDetailDTO, product, order, wrapper);

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
		// orderDetails를 가져와서 순회 돌면서 재고 복구 추가
		return ResponseEntity.ok().build();
	}

	/**
	 * 특정 주문 코드의 주문 상세 정보를 반환하는 메서드
	 */
	@Override
	public ResponseOrderWrapperDTO getOrderByOrderCode(String orderCode) {
		ResponseOrderDTO order = orderJpaRepository.findById(orderCode).map(ResponseOrderDTO::fromEntity).orElse(null);
		Payment payment = paymentJpaRepository.findByOrderOrderCode(orderCode).orElse(null);
		if (payment != null) {
			order.setPaymentMethod(payment.getPaymentMethod().getPaymentMethodName().name());
		}
		Member member = memberJpaRepository.findById(order.getCustomerId()).orElse(null);
		if (member != null) {
			order.setMemberId(member.getMemberId());
			order.setMember(true);
		} else {
			Customer customer = customerJpaRepository.findById(order.getCustomerId()).orElseThrow();
			order.setMemberId(customer.getCustomerEmail());
			order.setMember(false);
		}

		List<ResponseOrderDetailDTO> orderDetails = orderDetailJpaRepository.findByOrderOrderCode(orderCode)
			.stream().map(ResponseOrderDetailDTO::fromEntity).toList();
		return new ResponseOrderWrapperDTO(order, orderDetails);
	}

	@Override
	public Page<ResponseOrderDTO> getOrdersByMemberId(Pageable pageable, String memberId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		long customerId = member.getCustomerId();
		return orderJpaRepository.findAllByCustomer_CustomerIdOrderByOrderCreatedAtDesc(pageable, customerId)
			.map(ResponseOrderDTO::fromEntity);
	}

	/**
	 * 지금까지 총 주문 개수를 조회하는 메서드
	 */
	@Override
	public long getAllOrders() {
		return orderJpaRepository.countAllOrders();
	}

	/**
	 * 총 매출액 조회하는 메서드
	 */
	@Override
	public long getTotalSales() {
		Long totalSales = orderDetailJpaRepository.getTotalSales();

		return totalSales != null ? totalSales : 0L;
	}

	/**
	 * 이번 달 총 매출액 조회하는 메서드
	 */
	@Override
	public long getTotalMonthlySales() {
		YearMonth thisMonth = YearMonth.now();
		LocalDateTime start = thisMonth.atDay(1).atStartOfDay();
		LocalDateTime end = thisMonth.atEndOfMonth().atTime(LocalTime.MAX);

		Long totalMonthlySales = orderDetailJpaRepository.getTotalMonthlySales(start, end);

		return totalMonthlySales != null ? totalMonthlySales : 0L;
	}

	@Override
	public long getTotalDailySales() {
		LocalDateTime start = LocalDate.now().atStartOfDay();
		LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);

		Long totalDailySales = orderDetailJpaRepository.getTotalDailySales(start, end);

		return totalDailySales != null ? totalDailySales : 0L;
	}
}
