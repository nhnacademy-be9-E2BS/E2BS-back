package com.nhnacademy.back.order.order;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.coupon.membercoupon.service.MemberCouponService;
import com.nhnacademy.back.order.deliveryfee.domain.entity.DeliveryFee;
import com.nhnacademy.back.order.deliveryfee.repository.DeliveryFeeJpaRepository;
import com.nhnacademy.back.order.order.adaptor.PaymentAdaptorFactory;
import com.nhnacademy.back.order.order.adaptor.TossAdaptor;
import com.nhnacademy.back.order.order.adaptor.TossPaymentAdaptor;
import com.nhnacademy.back.order.order.adaptor.mapper.TossResponseMapper;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderDetailDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderReturnDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestOrderWrapperDTO;
import com.nhnacademy.back.order.order.model.dto.request.RequestPaymentApproveDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderResultDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderWrapperDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponsePaymentConfirmDTO;
import com.nhnacademy.back.order.order.model.dto.response.ResponseTossPaymentConfirmDTO;
import com.nhnacademy.back.order.order.model.entity.Order;
import com.nhnacademy.back.order.order.model.entity.OrderDetail;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.impl.OrderServiceImpl;
import com.nhnacademy.back.order.orderreturn.repository.OrderReturnJpaRepository;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderStateName;
import com.nhnacademy.back.order.orderstate.repository.OrderStateJpaRepository;
import com.nhnacademy.back.order.payment.domain.entity.Payment;
import com.nhnacademy.back.order.payment.repository.PaymentJpaRepository;
import com.nhnacademy.back.order.paymentmethod.domain.entity.PaymentMethod;
import com.nhnacademy.back.order.paymentmethod.domain.entity.PaymentMethodName;
import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;
import com.nhnacademy.back.order.wrapper.repository.WrapperJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.product.service.ProductService;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@InjectMocks
	private OrderServiceImpl orderService;

	@Mock
	private OrderJpaRepository orderJpaRepository;
	@Mock
	private OrderDetailJpaRepository orderDetailJpaRepository;
	@Mock
	private DeliveryFeeJpaRepository deliveryFeeJpaRepository;
	@Mock
	private CustomerJpaRepository customerJpaRepository;
	@Mock
	private ProductJpaRepository productJpaRepository;
	@Mock
	private OrderStateJpaRepository orderStateJpaRepository;
	@Mock
	private WrapperJpaRepository wrapperJpaRepository;
	@Mock
	private PaymentJpaRepository paymentJpaRepository;
	@Mock
	private MemberJpaRepository memberJpaRepository;
	@Mock
	private OrderReturnJpaRepository orderReturnJpaRepository;
	@Mock
	private TossAdaptor tossAdaptor;
	@Mock
	private ProductService productService;
	@Mock
	private PointHistoryService pointHistoryService;
	@Mock
	private MemberCouponService memberCouponService;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;
	@Mock
	private PaymentAdaptorFactory paymentAdaptorFactory;
	@Mock
	private TossPaymentAdaptor tossPaymentAdaptor;

	@Test
	@DisplayName("토스 결제 주문 생성 성공")
	void testCreateOrder_Success() {
		// given
		RequestOrderDTO orderDTO = new RequestOrderDTO();
		orderDTO.setCustomerId(1L);
		orderDTO.setDeliveryFeeId(1L);
		orderDTO.setOrderReceiverName("테스트");
		orderDTO.setOrderReceiverPhone("01012345678");
		orderDTO.setOrderAddressCode("12345");
		orderDTO.setOrderAddressInfo("서울시");
		orderDTO.setOrderAddressExtra("A동");
		orderDTO.setOrderPointAmount(5000L);
		orderDTO.setOrderPaymentStatus(true);
		orderDTO.setOrderPaymentAmount(15000L);

		RequestOrderDetailDTO detailDTO = new RequestOrderDetailDTO();
		detailDTO.setOrderCode("TEST-ORDER-CODE");
		detailDTO.setProductId(1L);
		detailDTO.setWrapperId(1L);
		detailDTO.setOrderQuantity(1);
		detailDTO.setOrderDetailPerPrice(15000L);

		RequestOrderWrapperDTO wrapperDTO = new RequestOrderWrapperDTO();
		wrapperDTO.setOrder(orderDTO);
		wrapperDTO.setOrderDetails(List.of(detailDTO));

		Customer customer = mock(Customer.class);
		DeliveryFee deliveryFee = mock(DeliveryFee.class);
		Product product = mock(Product.class);
		OrderState orderState = mock(OrderState.class);
		Wrapper wrapper = mock(Wrapper.class);
		ProductState productState = mock(ProductState.class);

		when(customerJpaRepository.findById(anyLong())).thenReturn(Optional.of(customer));
		when(deliveryFeeJpaRepository.findById(anyLong())).thenReturn(Optional.of(deliveryFee));
		when(productJpaRepository.findById(anyLong())).thenReturn(Optional.of(product));
		when(orderStateJpaRepository.findByOrderStateName(any(OrderStateName.class))).thenReturn(
			Optional.of(orderState));
		when(wrapperJpaRepository.findById(anyLong())).thenReturn(Optional.of(wrapper));
		when(product.getProductState()).thenReturn(productState);
		when(productState.getProductStateName()).thenReturn(ProductStateName.SALE);

		// when
		ResponseEntity<ResponseOrderResultDTO> response = orderService.createOrder(wrapperDTO);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(orderJpaRepository).save(any(Order.class));
		verify(orderDetailJpaRepository).save(any(OrderDetail.class));
	}

	@Test
	@DisplayName("포인트 주문 시 결제 성공")
	void testCreatePointOrder_Success() {
		// given
		RequestOrderDTO orderDTO = new RequestOrderDTO();
		orderDTO.setCustomerId(1L);
		orderDTO.setDeliveryFeeId(1L);
		orderDTO.setOrderReceiverName("테스트");
		orderDTO.setOrderReceiverPhone("01012345678");
		orderDTO.setOrderAddressCode("12345");
		orderDTO.setOrderAddressInfo("서울시");
		orderDTO.setOrderAddressExtra("A동");
		orderDTO.setOrderPointAmount(5000L);
		orderDTO.setOrderPaymentStatus(true);
		orderDTO.setOrderPaymentAmount(15000L);

		RequestOrderDetailDTO detailDTO = new RequestOrderDetailDTO();
		detailDTO.setOrderCode("TEST-ORDER-CODE");
		detailDTO.setProductId(1L);
		detailDTO.setWrapperId(1L);
		detailDTO.setOrderQuantity(1);
		detailDTO.setOrderDetailPerPrice(15000L);

		RequestOrderWrapperDTO wrapperDTO = new RequestOrderWrapperDTO();
		wrapperDTO.setOrder(orderDTO);
		wrapperDTO.setOrderDetails(List.of(detailDTO));

		Customer customer = mock(Customer.class);
		DeliveryFee deliveryFee = mock(DeliveryFee.class);
		Product product = mock(Product.class);
		OrderState orderState = mock(OrderState.class);
		Wrapper wrapper = mock(Wrapper.class);

		when(customerJpaRepository.findById(anyLong())).thenReturn(Optional.of(customer));
		when(deliveryFeeJpaRepository.findById(anyLong())).thenReturn(Optional.of(deliveryFee));
		when(productJpaRepository.findById(anyLong())).thenReturn(Optional.of(product));
		when(orderStateJpaRepository.findByOrderStateName(any(OrderStateName.class))).thenReturn(
			Optional.of(orderState));
		when(wrapperJpaRepository.findById(anyLong())).thenReturn(Optional.of(wrapper));

		Order order = mock(Order.class);
		ProductState productState = mock(ProductState.class);

		when(orderJpaRepository.findById(anyString())).thenReturn(Optional.of(order));
		when(order.getCustomer()).thenReturn(customer);
		when(customer.getCustomerId()).thenReturn(1L);
		doNothing().when(order).updatePaymentStatus(true);
		when(product.getProductState()).thenReturn(productState);
		when(productState.getProductStateName()).thenReturn(ProductStateName.SALE);

		// when
		ResponseEntity<ResponseOrderResultDTO> response = orderService.createPointOrder(wrapperDTO);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(orderJpaRepository, times(1)).save(any(Order.class));
		verify(orderDetailJpaRepository).save(any(OrderDetail.class));
		verify(order).updatePaymentStatus(true);

	}

	@Test
	@DisplayName("토스 주문 시 결제 승인 성공")
	void testConfirmOrder_Success() {
		// given
		String orderId = "TEST-ORDER-CODE";
		String paymentKey = "TEST-PAYMENT-KEY";
		long amount = 15000L;
		RequestPaymentApproveDTO approveDTO = new RequestPaymentApproveDTO(orderId, paymentKey, amount, "TOSS");
		Order order = mock(Order.class);
		Customer customer = mock(Customer.class);
		ResponseEntity<ResponsePaymentConfirmDTO> responseEntity = ResponseEntity.ok(new ResponsePaymentConfirmDTO());

		when(tossPaymentAdaptor.confirmOrder(any()))
			.thenReturn(responseEntity);
		when(orderJpaRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(order.getMemberCoupon()).thenReturn(null);

		when(order.getCustomer()).thenReturn(customer);
		when(customer.getCustomerId()).thenReturn(1L);

		when(paymentAdaptorFactory.getAdapter(any())).thenReturn(tossPaymentAdaptor);
		// when
		ResponseEntity<ResponsePaymentConfirmDTO> response =
			orderService.confirmOrder(approveDTO);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(order).updatePaymentStatus(true);
	}

	@Test
	@DisplayName("토스 주문 시 결제 승인 실패")
	void testConfirmOrder_Failure() {
		// given
		String orderId = "TEST-ORDER-CODE";
		String paymentKey = "TEST-PAYMENT-KEY";
		long amount = 15000L;
		RequestPaymentApproveDTO approveDTO = new RequestPaymentApproveDTO(orderId, paymentKey, amount, "TOSS");

		ResponseEntity<ResponsePaymentConfirmDTO> responseEntity =
			ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		when(paymentAdaptorFactory.getAdapter(any())).thenReturn(tossPaymentAdaptor);
		when(tossPaymentAdaptor.confirmOrder(any())).thenReturn(responseEntity);

		// when
		ResponseEntity<ResponsePaymentConfirmDTO> response =
			orderService.confirmOrder(approveDTO);

		// then
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		verify(orderJpaRepository, never()).save(any());
	}

	@Test
	@DisplayName("주문서 삭제 요청 성공")
	void testDeleteOrder_Success() {
		// given
		String orderId = "TEST-ORDER-CODE";

		// when
		ResponseEntity<Void> response = orderService.deleteOrder(orderId);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(orderDetailJpaRepository).deleteByOrderOrderCode(orderId);
		verify(orderJpaRepository).deleteById(orderId);
	}

	@Test
	@DisplayName("주문 상세 정보 조회 요청")
	void testGetOrderByOrderCode_Success() {
		// given
		String orderCode = "TEST-ORDER-CODE";
		long customerId = 1L;

		// Order
		Customer customer = mock(Customer.class);
		Order orderEntity = mock(Order.class);
		DeliveryFee deliveryFee = mock(DeliveryFee.class);
		OrderState orderState = mock(OrderState.class);

		when(orderJpaRepository.findById(orderCode))
			.thenReturn(Optional.of(orderEntity));

		when(orderEntity.getCustomer()).thenReturn(customer);
		when(customer.getCustomerId()).thenReturn(customerId);
		when(orderEntity.getDeliveryFee()).thenReturn(deliveryFee);
		when(deliveryFee.getDeliveryFeeId()).thenReturn(1L);
		when(deliveryFee.getDeliveryFeeAmount()).thenReturn(1L);
		when(deliveryFee.getDeliveryFeeFreeAmount()).thenReturn(1L);
		when(orderEntity.getOrderState()).thenReturn(orderState);
		when(orderState.getOrderStateName()).thenReturn(OrderStateName.WAIT);
		// Payment
		Payment payment = mock(Payment.class);
		PaymentMethod paymentMethod = mock(PaymentMethod.class);
		when(paymentJpaRepository.findByOrderOrderCode(orderCode))
			.thenReturn(Optional.of(payment));
		when(payment.getPaymentMethod()).thenReturn(paymentMethod);
		when(paymentMethod.getPaymentMethodName()).thenReturn(PaymentMethodName.TOSS);

		// Member
		Member member = mock(Member.class);
		when(memberJpaRepository.findById(customerId))
			.thenReturn(Optional.of(member));

		// OrderDetails
		OrderDetail orderDetail = mock(OrderDetail.class);
		List<OrderDetail> orderDetailList = List.of(orderDetail);
		Product product = mock(Product.class);
		when(orderDetailJpaRepository.findByOrderOrderCode(orderCode))
			.thenReturn(orderDetailList);

		when(orderDetail.getProduct()).thenReturn(product);
		when(product.getProductId()).thenReturn(1L);
		when(product.getProductTitle()).thenReturn("Title");

		// when
		ResponseOrderWrapperDTO result = orderService.getOrderByOrderCode(orderCode);

		// then
		assertNotNull(result);
		assertEquals(1, result.getOrderDetails().size());
	}

	@Test
	@DisplayName("특정 회원 주문 내역 조회")
	void testGetOrdersByMemberId() {
		// given
		Member member = mock(Member.class);
		Pageable pageable = PageRequest.of(0, 10);
		Order order = mock(Order.class);
		ResponseOrderDTO responseOrderDTO = mock(ResponseOrderDTO.class);
		Page<Order> orderPage = new PageImpl<>(List.of(order));
		when(memberJpaRepository.getMemberByMemberId(anyString())).thenReturn(member);
		when(member.getCustomerId()).thenReturn(1L);
		when(orderJpaRepository.findAllByCustomer_CustomerIdOrderByOrderCreatedAtDesc(pageable, 1L)).thenReturn(
			orderPage);
		try (MockedStatic<ResponseOrderDTO> mockedStatic = Mockito.mockStatic(ResponseOrderDTO.class)) {
			mockedStatic.when(() -> ResponseOrderDTO.fromEntity(order)).thenReturn(responseOrderDTO);

			// when
			Page<ResponseOrderDTO> result = orderService.getOrdersByMemberId(pageable, anyString(), null, null, null,
				null);

			// then
			assertThat(result).isNotNull();
			assertEquals(1, result.getContent().size());
			verify(orderJpaRepository).findAllByCustomer_CustomerIdOrderByOrderCreatedAtDesc(pageable, 1L);
		}
	}
	//
	// @Test
	// @DisplayName("특정 회원 주문 내역 조회(주문 상태 검색)")
	// void testGetOrdersByMemberIdWithOrderState() {
	// 	// given
	// 	Member member = mock(Member.class);
	// 	Pageable pageable = PageRequest.of(0, 10);
	// 	Order order = mock(Order.class);
	// 	OrderState orderState = mock(OrderState.class);
	// 	ResponseOrderDTO responseOrderDTO = mock(ResponseOrderDTO.class);
	// 	Page<Order> orderPage = new PageImpl<>(List.of(order));
	// 	when(memberJpaRepository.getMemberByMemberId(anyString())).thenReturn(member);
	// 	when(member.getCustomerId()).thenReturn(1L);
	// 	when(orderJpaRepository.findAllByCustomer_CustomerIdAndOrderStateOrderByOrderCreatedAtDesc(pageable, 1L,
	// 		orderState)).thenReturn(orderPage);
	// 	when(orderStateJpaRepository.findByOrderStateName(any())).thenReturn(Optional.ofNullable(orderState));
	// 	try (MockedStatic<ResponseOrderDTO> mockedStatic = Mockito.mockStatic(ResponseOrderDTO.class)) {
	// 		mockedStatic.when(() -> ResponseOrderDTO.fromEntity(order)).thenReturn(responseOrderDTO);
	//
	// 		// when
	// 		Page<ResponseOrderDTO> result = orderService.getOrdersByMemberId(pageable, anyString(), "WAIT", null, null,
	// 			null);
	//
	// 		// then
	// 		assertThat(result).isNotNull();
	// 		assertEquals(1, result.getContent().size());
	// 		verify(orderJpaRepository).findAllByCustomer_CustomerIdAndOrderStateOrderByOrderCreatedAtDesc(pageable, 1L,
	// 			orderState);
	// 	}
	// }

	@Test
	@DisplayName("특정 회원 주문 내역 조회(주문 날짜 검색)")
	void testGetOrdersByMemberIdWithDate() {
		// given
		Member member = mock(Member.class);
		Pageable pageable = PageRequest.of(0, 10);
		Order order = mock(Order.class);
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusDays(1);
		ResponseOrderDTO responseOrderDTO = mock(ResponseOrderDTO.class);
		Page<Order> orderPage = new PageImpl<>(List.of(order));
		when(memberJpaRepository.getMemberByMemberId(anyString())).thenReturn(member);
		when(member.getCustomerId()).thenReturn(1L);
		when(orderJpaRepository.findAllByCustomer_CustomerIdAndOrderCreatedAtBetweenOrderByOrderCreatedAtDesc(pageable,
			1L, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))).thenReturn(orderPage);
		try (MockedStatic<ResponseOrderDTO> mockedStatic = Mockito.mockStatic(ResponseOrderDTO.class)) {
			mockedStatic.when(() -> ResponseOrderDTO.fromEntity(order)).thenReturn(responseOrderDTO);

			// when
			Page<ResponseOrderDTO> result = orderService.getOrdersByMemberId(pageable, anyString(), null, startDate,
				endDate, null);

			// then
			assertThat(result).isNotNull();
			assertEquals(1, result.getContent().size());
			verify(orderJpaRepository).findAllByCustomer_CustomerIdAndOrderCreatedAtBetweenOrderByOrderCreatedAtDesc(
				pageable, 1L, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
		}
	}

	// @Test
	// @DisplayName("특정 회원 주문 내역 조회(주문 코드 검색)")
	// void testGetOrdersByMemberIdWithOrderCode() {
	// 	// given
	// 	Member member = mock(Member.class);
	// 	Pageable pageable = PageRequest.of(0, 10);
	// 	Order order = mock(Order.class);
	// 	String orderCode = "TEST-ORDER-CODE";
	// 	ResponseOrderDTO responseOrderDTO = mock(ResponseOrderDTO.class);
	// 	Page<Order> orderPage = new PageImpl<>(List.of(order));
	// 	when(memberJpaRepository.getMemberByMemberId(anyString())).thenReturn(member);
	// 	when(member.getCustomerId()).thenReturn(1L);
	// 	when(orderJpaRepository.searchByCustomerIdAndOrderCodeIgnoreCase(1L, orderCode, pageable)).thenReturn(
	// 		orderPage);
	// 	try (MockedStatic<ResponseOrderDTO> mockedStatic = Mockito.mockStatic(ResponseOrderDTO.class)) {
	// 		mockedStatic.when(() -> ResponseOrderDTO.fromEntity(order)).thenReturn(responseOrderDTO);
	//
	// 		// when
	// 		Page<ResponseOrderDTO> result = orderService.getOrdersByMemberId(pageable, anyString(), null, null,
	// 			null, orderCode);
	//
	// 		// then
	// 		assertThat(result).isNotNull();
	// 		assertEquals(1, result.getContent().size());
	// 		verify(orderJpaRepository).searchByCustomerIdAndOrderCodeIgnoreCase(1L, orderCode, pageable);
	// 	}
	// }

	@Test
	@DisplayName("배송 대기 상태인 상품의 주문 취소(외부 결제 X)")
	void testCancelOrder() {
		String orderCode = "TEST-ORDER-CODE";
		Order order = mock(Order.class);
		OrderState orderState = mock(OrderState.class);
		OrderState cancelOrderState = mock(OrderState.class);
		Customer customer = mock(Customer.class);

		when(orderJpaRepository.findById(orderCode)).thenReturn(Optional.of(order));
		when(order.getOrderState()).thenReturn(orderState);
		when(orderState.getOrderStateName()).thenReturn(OrderStateName.WAIT);
		when(order.isOrderPaymentStatus()).thenReturn(true);
		when(orderStateJpaRepository.findByOrderStateName(OrderStateName.CANCEL)).thenReturn(
			Optional.ofNullable(cancelOrderState));
		when(order.getOrderPointAmount()).thenReturn(1000L);
		when(order.getMemberCoupon()).thenReturn(null);
		when(paymentJpaRepository.findByOrderOrderCode(orderCode)).thenReturn(Optional.empty());

		when(order.getCustomer()).thenReturn(customer);
		when(customer.getCustomerId()).thenReturn(1L);
		ResponseEntity<Void> response = orderService.cancelOrder(orderCode);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(order).updateOrderState(cancelOrderState);

	}

	@Test
	@DisplayName("배송 대기 상태인 상품의 주문 취소(외부 결제 O)")
	void testCancelOrderWithPaymentMethod() {
		String orderCode = "TEST-ORDER-CODE";
		Order order = mock(Order.class);
		Customer customer = mock(Customer.class);
		OrderState orderState = mock(OrderState.class);
		OrderState cancelOrderState = mock(OrderState.class);
		Payment payment = mock(Payment.class);
		PaymentMethod paymentMethod = mock(PaymentMethod.class);

		when(orderJpaRepository.findById(orderCode)).thenReturn(Optional.of(order));
		when(order.getOrderState()).thenReturn(orderState);
		when(orderState.getOrderStateName()).thenReturn(OrderStateName.WAIT);
		when(order.isOrderPaymentStatus()).thenReturn(true);
		when(orderStateJpaRepository.findByOrderStateName(OrderStateName.CANCEL)).thenReturn(
			Optional.ofNullable(cancelOrderState));
		when(order.getOrderPointAmount()).thenReturn(1000L);
		when(order.getMemberCoupon()).thenReturn(null);
		when(paymentJpaRepository.findByOrderOrderCode(orderCode)).thenReturn(Optional.of(payment));
		when(payment.getPaymentKey()).thenReturn("TEST-PAYMENT-KEY");
		when(payment.getPaymentMethod()).thenReturn(paymentMethod);
		when(payment.getTotalPaymentAmount()).thenReturn(1000L);
		when(paymentMethod.getPaymentMethodName()).thenReturn(PaymentMethodName.TOSS);
		when(paymentAdaptorFactory.getAdapter(any())).thenReturn(
			new TossPaymentAdaptor(tossAdaptor, new TossResponseMapper()));
		when(tossAdaptor.cancelOrder(any(), any(), any())).thenReturn(
			ResponseEntity.ok(new ResponseTossPaymentConfirmDTO()));

		when(order.getCustomer()).thenReturn(customer);
		when(customer.getCustomerId()).thenReturn(1L);
		ResponseEntity<Void> response = orderService.cancelOrder(orderCode);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(order).updateOrderState(cancelOrderState);

	}

	@Test
	@DisplayName("비회원 주문 내역 조회")
	void testGetOrdersByCustomerId() {
		Order order = mock(Order.class);
		ResponseOrderDTO responseOrderDTO = mock(ResponseOrderDTO.class);
		Page<Order> orderPage = new PageImpl<>(List.of(order));
		Pageable pageable = PageRequest.of(0, 10);
		when(orderJpaRepository.findAllByCustomer_CustomerIdOrderByOrderCreatedAtDesc(pageable, 1L)).thenReturn(
			orderPage);
		try (MockedStatic<ResponseOrderDTO> mockedStatic = Mockito.mockStatic(ResponseOrderDTO.class)) {
			mockedStatic.when(() -> ResponseOrderDTO.fromEntity(order)).thenReturn(responseOrderDTO);

			// when
			Page<ResponseOrderDTO> result = orderService.getOrdersByCustomerId(pageable, 1L);

			// then
			assertThat(result).isNotNull();
			assertEquals(1, result.getContent().size());
			verify(orderJpaRepository).findAllByCustomer_CustomerIdOrderByOrderCreatedAtDesc(pageable, 1L);
		}
	}

	@Test
	@DisplayName("반품 요청 처리")
	void testReturnOrder() {
		Order order = mock(Order.class);
		Customer customer = mock(Customer.class);
		RequestOrderReturnDTO returnDTO = new RequestOrderReturnDTO("TEST-ORDER-CODE", "BREAK", "BREAK");
		OrderState orderState = mock(OrderState.class);
		when(orderJpaRepository.findById("TEST-ORDER-CODE")).thenReturn(Optional.of(order));
		when(order.getOrderState()).thenReturn(orderState);
		when(orderState.getOrderStateName()).thenReturn(OrderStateName.COMPLETE);
		when(order.getOrderPaymentAmount()).thenReturn(1000L);
		when(order.getOrderPointAmount()).thenReturn(1000L);
		when(order.getCustomer()).thenReturn(customer);
		when(customer.getCustomerId()).thenReturn(1L);

		ResponseEntity<Void> response = orderService.returnOrder(returnDTO);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
