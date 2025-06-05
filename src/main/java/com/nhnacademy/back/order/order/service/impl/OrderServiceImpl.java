package com.nhnacademy.back.order.order.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;
import com.nhnacademy.back.coupon.membercoupon.repository.MemberCouponJpaRepository;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;
import com.nhnacademy.back.event.event.OrderCancelPointEvent;
import com.nhnacademy.back.event.event.OrderCancelPointPaymentEvent;
import com.nhnacademy.back.event.event.OrderPointEvent;
import com.nhnacademy.back.event.event.OrderPointPaymentEvent;
import com.nhnacademy.back.order.deliveryfee.domain.entity.DeliveryFee;
import com.nhnacademy.back.order.deliveryfee.repository.DeliveryFeeJpaRepository;
import com.nhnacademy.back.order.order.adaptor.TossAdaptor;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderDetailDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderReturnDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestTossCancelDTO;
import com.nhnacademy.back.order.order.domain.dto.request.RequestTossConfirmDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderDetailDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseOrderWrapperDTO;
import com.nhnacademy.back.order.order.domain.dto.response.ResponseTossPaymentConfirmDTO;
import com.nhnacademy.back.order.order.domain.entity.Order;
import com.nhnacademy.back.order.order.domain.entity.OrderDetail;
import com.nhnacademy.back.order.order.exception.OrderNotFoundException;
import com.nhnacademy.back.order.order.exception.OrderProcessException;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.OrderService;
import com.nhnacademy.back.order.orderreturn.domain.entity.OrderReturn;
import com.nhnacademy.back.order.orderreturn.repository.OrderReturnJpaRepository;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderStateName;
import com.nhnacademy.back.order.orderstate.repository.OrderStateJpaRepository;
import com.nhnacademy.back.order.payment.domain.entity.Payment;
import com.nhnacademy.back.order.payment.repository.PaymentJpaRepository;
import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;
import com.nhnacademy.back.order.wrapper.exception.WrapperNotFoundException;
import com.nhnacademy.back.order.wrapper.repository.WrapperJpaRepository;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicyType;
import com.nhnacademy.back.pointpolicy.repository.PointPolicyJpaRepository;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
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
	private final PointPolicyJpaRepository pointPolicyJpaRepository;
	private final OrderReturnJpaRepository orderReturnJpaRepository;

	private final ProductService productService;
	private final PointHistoryService pointHistoryService;
	private final MemberCouponService memberCouponService;

	private final TossAdaptor tossAdaptor;

	private final ApplicationEventPublisher eventPublisher;

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
	 */
	@Transactional
	@Override
	public ResponseEntity<ResponseOrderResultDTO> createPointOrder(RequestOrderWrapperDTO requestOrderWrapperDTO) {
		ResponseEntity<ResponseOrderResultDTO> response = saveOrder(requestOrderWrapperDTO);
		//포인트 차감, 적립 요청, 쿠폰 사용 요청, 결제 여부 최신화

		Order order = orderJpaRepository.findById(Objects.requireNonNull(response.getBody()).getOrderId())
			.orElseThrow(OrderNotFoundException::new);

		// 포인트 차감
		eventPublisher.publishEvent(new OrderPointPaymentEvent(order.getCustomer().getCustomerId(), order.getOrderPointAmount()));

		// 포인트 적립
		eventPublisher.publishEvent(new OrderPointEvent(order.getCustomer().getCustomerId(), order.getOrderRewardAmount()));

		// 쿠폰 사용 처리
		if (order.getMemberCoupon() != null) {
			memberCouponService.updateMemberCouponById(order.getMemberCoupon().getMemberCouponId());
		}
		// 결제 완료로 상태 업데이트
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
		Customer customer = customerJpaRepository.findById(requestOrderDTO.getCustomerId())
			.orElseThrow(CustomerNotFoundException::new);
		Member member = memberJpaRepository.findByCustomer(customer).orElse(null);
		MemberCoupon memberCoupon = null;
		// 사용한 쿠폰이 있는 경우에만 Repository 검색, 사용 가능한 쿠폰이 맞는지도 봐야함
		if (requestOrderDTO.getMemberCouponId() != null) {
			memberCoupon = memberCouponJpaRepository.findById(requestOrderDTO.getMemberCouponId())
				.orElseThrow(() -> new OrderProcessException("쿠폰이 존재하지 않습니다."));
			if (memberCoupon.isMemberCouponUsed()) {
				// 사용된 쿠폰인 경우로 예외 발생, 지금 이거만으로는 만료된 쿠폰인가? 알 수 없음
				throw new OrderProcessException("이미 사용된 쿠폰입니다.");
			}
		}

		DeliveryFee deliveryFee = deliveryFeeJpaRepository.findById(requestOrderDTO.getDeliveryFeeId())
			.orElseThrow(() -> new OrderProcessException("존재하지 않는 배송비 정책입니다."));
		OrderState orderState = orderStateJpaRepository.findByOrderStateName(OrderStateName.WAIT).orElse(null);

		//적립해야할 금액을 미리 계산해서 넣어야 함(기본 적립률 + 등급 적립률)
		long rewardRate = 0L;
		if (member != null) {

			if (pointHistoryService.getMemberPoints(member.getMemberId()).getPointAmount()
				< requestOrderDTO.getOrderPointAmount()) {
				//만약 포인트 사용 금액이 가지고 있는 포인트보다 많으면 예외 발생
				throw new OrderProcessException("포인트가 부족합니다.");
			}

			// 회원 등급 적립률
			int rankRate = member.getMemberRank().getMemberRankTierBonusRate();
			// 기본 적립률
			long defaultRate = pointPolicyJpaRepository.findByPointPolicyTypeAndPointPolicyIsActive(
				PointPolicyType.BOOK, true).getPointPolicyFigure();
			rewardRate = defaultRate + rankRate;

		}

		long rewardAmount = requestOrderDTO.getOrderPureAmount() * rewardRate / 100;
		Order order = new Order(requestOrderDTO, memberCoupon, deliveryFee, customer, orderState, rewardAmount);
		orderJpaRepository.save(order);

		// 주문 상세 저장
		String orderCode = order.getOrderCode();
		List<RequestOrderDetailDTO> requestOrderDetailDTOs = requestOrderWrapperDTO.getOrderDetails();

		// 실제 주문서 ID와 맞추기
		requestOrderDetailDTOs.forEach(orderDetailDTO -> orderDetailDTO.setOrderCode(orderCode));

		for (RequestOrderDetailDTO requestOrderDetailDTO : requestOrderDetailDTOs) {
			Product product = productJpaRepository.findById(requestOrderDetailDTO.getProductId())
				.orElseThrow(ProductNotFoundException::new);
			Wrapper wrapper = wrapperJpaRepository.findById(requestOrderDetailDTO.getWrapperId())
				.orElseThrow(() -> new WrapperNotFoundException(
					"없는 포장지 입니다: " + requestOrderDetailDTO.getWrapperId()));
			OrderDetail orderDetail = new OrderDetail(requestOrderDetailDTO, product, order, wrapper);
			// 재고 선 차감
			productService.updateProductStock(product.getProductId(),
				new RequestProductStockUpdateDTO(-requestOrderDetailDTO.getOrderQuantity()));
			orderDetailJpaRepository.save(orderDetail);
		}

		return ResponseEntity.ok(new ResponseOrderResultDTO(orderCode, order.getOrderPaymentAmount()));
	}

	/**
	 * 결제 완료 시 토스에 결제 승인 요청을 보내는 서비스
	 */
	@Transactional
	@Override
	public ResponseEntity<ResponseTossPaymentConfirmDTO> confirmOrder(String orderId, String paymentKey, long amount) {
		RequestTossConfirmDTO requestTossConfirmDTO = new RequestTossConfirmDTO(orderId, paymentKey, amount);
		// 결제 승인 결과를 받아 온 응답
		ResponseEntity<ResponseTossPaymentConfirmDTO> response = tossAdaptor.confirmOrder(requestTossConfirmDTO,
			secretKey);
		// 만약 승인된 경우 결제 상태 업데이트, 포인트 차감, 적립, 쿠폰 사용
		if (response.getStatusCode().is2xxSuccessful()) {
			Order order = orderJpaRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
			// 포인트 차감
			eventPublisher.publishEvent(new OrderPointPaymentEvent(order.getCustomer().getCustomerId(), order.getOrderPointAmount()));

			// 포인트 적립
			eventPublisher.publishEvent(new OrderPointEvent(order.getCustomer().getCustomerId(), order.getOrderRewardAmount()));

			// 쿠폰 사용 처리
			if (order.getMemberCoupon() != null) {
				memberCouponService.updateMemberCouponById(order.getMemberCoupon().getMemberCouponId());
			}
			// 결제 완료로 상태 업데이트
			order.updatePaymentStatus(true);
		}
		return response;
	}

	/**
	 * 주문서 저장 후 외부 결제 모달을 끈 경우, 주문서를 제거
	 * 아직 결제 승인을 하지 않았으므로 쿠폰, 포인트를 복구할 필요 없이 재고만 복구한다.
	 */
	@Transactional
	@Override
	public ResponseEntity<Void> deleteOrder(String orderId) {
		List<OrderDetail> orderDetails = orderDetailJpaRepository.findByOrderOrderCode(orderId);
		// orderDetails를 가져와서 순회 돌면서 재고 복구
		for (OrderDetail orderDetail : orderDetails) {
			productService.updateProductStock(orderDetail.getProduct().getProductId(),
				new RequestProductStockUpdateDTO(orderDetail.getOrderQuantity()));
		}
		orderDetailJpaRepository.deleteByOrderOrderCode(orderId);
		orderJpaRepository.deleteById(orderId);
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
			Customer customer = customerJpaRepository.findById(order.getCustomerId())
				.orElseThrow(CustomerNotFoundException::new);
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

	@Override
	public Page<ResponseOrderDTO> getOrdersByCustomerId(Pageable pageable, long customerId) {
		return orderJpaRepository.findAllByCustomer_CustomerIdOrderByOrderCreatedAtDesc(pageable, customerId)
			.map(ResponseOrderDTO::fromEntity);
	}

	@Override
	@Transactional
	public ResponseEntity<Void> cancelOrder(String orderCode) {
		// 주문 코드로 주문서의 상태 취소로 변경
		// 사용한 포인트, 쿠폰 복구
		// 이후 결제 테이블에서 주문 코드로 검색하여 있다면 취소 요청
		Order order = orderJpaRepository.findById(orderCode).orElseThrow(OrderNotFoundException::new);

		if (!order.getOrderState().getOrderStateName().equals(OrderStateName.WAIT)) {
			// 주문이 대기 상태가 아닌 경우 예외 발생
			throw new OrderProcessException("대기 상태의 주문이 아닙니다.");
		}

		// 주문 결제 승인이 되지 않은 경우, 포인트 차감, 적립, 쿠폰 사용 X,
		// 승인 된 경우에만 외부 결제 있는지 확인 및 적립 포인트를 회수
		// 이미 프론트에서 결제 안할 시 주문 취소 버튼이 비활성화됨
		if (!order.isOrderPaymentStatus()) {
			// 만약 결제 승인 안됐을 시 배치에서 삭제할 때까지 삭제 못함, 직접 요청 시 예외 발생 예정
			throw new OrderProcessException("결제되지 않은 주문입니다.");
		}

		OrderState orderState = orderStateJpaRepository.findByOrderStateName(OrderStateName.CANCEL).orElse(null);
		order.updateOrderState(orderState);

		long usedPoint = order.getOrderPointAmount();
		log.info("usedPoint:{}", usedPoint);
		// 사용한 포인트 수치만큼 복구 요청
		eventPublisher.publishEvent(new OrderCancelPointPaymentEvent(order.getCustomer().getCustomerId(), usedPoint));

		MemberCoupon memberCoupon = order.getMemberCoupon();
		if (memberCoupon != null) {
			// 사용한 쿠폰이 있다면 똑같은 쿠폰을 새로 발급 요청
			memberCouponService.reIssueCouponById(memberCoupon.getMemberCouponId());
		}

		// 재고 복구
		List<OrderDetail> orderDetails = orderDetailJpaRepository.findByOrderOrderCode(orderCode);
		for (OrderDetail orderDetail : orderDetails) {
			productService.updateProductStock(orderDetail.getProduct().getProductId(),
				new RequestProductStockUpdateDTO(orderDetail.getOrderQuantity()));
		}

		//적립된 포인트 회수 요청
		eventPublisher.publishEvent(new OrderCancelPointEvent(order.getCustomer().getCustomerId(), order.getOrderRewardAmount()));

		// 주문 코드에 해당하는 외부 API 결제 내역이 있는지 확인, 있다면 결제 취소 요청
		Payment payment = paymentJpaRepository.findByOrderOrderCode(orderCode).orElse(null);
		if (payment != null) {
			ResponseEntity<ResponseTossPaymentConfirmDTO> tossResponse = tossAdaptor.cancelOrder(
				payment.getPaymentKey(),
				new RequestTossCancelDTO("구매자 변심", payment.getTotalPaymentAmount()),
				secretKey);

			return ResponseEntity.status(tossResponse.getStatusCode()).build();
		}

		return ResponseEntity.ok().build();
	}

	@Override
	@Transactional
	public ResponseEntity<Void> returnOrder(RequestOrderReturnDTO returnDTO) {
		Order order = orderJpaRepository.findById(returnDTO.getOrderCode()).orElseThrow(OrderNotFoundException::new);
		if (!order.getOrderState().getOrderStateName().equals(OrderStateName.COMPLETE)) {
			throw new OrderProcessException("반품할 수 있는 주문이 아닙니다.");
		}

		long returnAmount = 0;
		if (returnDTO.getReturnCategory().equals("BREAK")) {
			//파손, 불량의 경우 배송비, 포장지 포함 전체 금액 환불
			returnAmount = order.getOrderPointAmount() + order.getOrderPaymentAmount();
		} else if (returnDTO.getReturnCategory().equals("CHANGE_MIND")) {
			// 단순 변심일 시 내야하는 반품 배송비
			long deliveryFee = deliveryFeeJpaRepository.findTopByOrderByDeliveryFeeDateDesc().getDeliveryFeeAmount();
			// 단순 변심에는 순수 상품 금액 - 쿠폰 할인가만 환불한다.
			returnAmount = order.getOrderPureAmount() - deliveryFee;
		}

		// 배송비를 제외한 금액이 0원 이하라면 반품이 불가함
		if (returnAmount <= 0) {
			throw new OrderProcessException("반품 금액이 0원 이하입니다.");
		}
		// 포인트 환불
		// 적립 금액 회수

		// 사용한 쿠폰이 있을 시 쿠폰 복구
		MemberCoupon memberCoupon = order.getMemberCoupon();
		if (memberCoupon != null) {
			memberCouponService.reIssueCouponById(memberCoupon.getMemberCouponId());
		}

		//재고 복구?

		OrderReturn orderReturn = new OrderReturn(returnDTO, order, returnAmount);
		orderReturnJpaRepository.save(orderReturn);

		OrderState orderState = orderStateJpaRepository.findByOrderStateName(OrderStateName.RETURN).orElse(null);
		order.updateOrderState(orderState);
		return ResponseEntity.ok().build();
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
