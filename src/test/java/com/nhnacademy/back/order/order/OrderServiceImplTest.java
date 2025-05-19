package com.nhnacademy.back.order.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
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
import com.nhnacademy.back.order.order.service.impl.OrderServiceImpl;
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
	private TossConfirmAdaptor tossConfirmAdaptor;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(orderService, "secretKey", "dummy-secret");
	}

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

		when(customerJpaRepository.findById(anyLong())).thenReturn(Optional.of(customer));
		when(deliveryFeeJpaRepository.findById(anyLong())).thenReturn(Optional.of(deliveryFee));
		when(productJpaRepository.findById(anyLong())).thenReturn(Optional.of(product));
		when(orderStateJpaRepository.findByOrderStateName(any(OrderStateName.class))).thenReturn(
			Optional.of(orderState));
		when(wrapperJpaRepository.findById(anyLong())).thenReturn(Optional.of(wrapper));

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

		when(orderJpaRepository.findById(anyString())).thenReturn(Optional.of(order));
		doNothing().when(order).updatePaymentStatus(true);

		// 스파이로 createOrder() 모킹
		OrderServiceImpl spyService = Mockito.spy(orderService);

		// when
		ResponseEntity<ResponseOrderResultDTO> response = spyService.createPointOrder(wrapperDTO);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		// saveOrder에서 한 번, 이후 결제 완료 상태 변경에서 한 번으로 2번 진행
		verify(orderJpaRepository, times(2)).save(any(Order.class));
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

		Order order = mock(Order.class);
		ResponseTossPaymentConfirmDTO confirmDTO = new ResponseTossPaymentConfirmDTO();
		ResponseEntity<ResponseTossPaymentConfirmDTO> responseEntity = ResponseEntity.ok(confirmDTO);

		when(tossConfirmAdaptor.confirmOrder(any(RequestTossConfirmDTO.class), anyString()))
			.thenReturn(responseEntity);
		when(orderJpaRepository.findById(orderId)).thenReturn(Optional.of(order));

		// when
		ResponseEntity<ResponseTossPaymentConfirmDTO> response =
			orderService.confirmOrder(orderId, paymentKey, amount);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(order).updatePaymentStatus(true);
		verify(orderJpaRepository).save(order);
	}

	@Test
	@DisplayName("토스 주문 시 결제 승인 실패")
	void testConfirmOrder_Failure() {
		// given
		String orderId = "TEST-ORDER-CODE";
		String paymentKey = "TEST-PAYMENT-KEY";
		long amount = 15000L;

		ResponseEntity<ResponseTossPaymentConfirmDTO> responseEntity =
			ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

		when(tossConfirmAdaptor.confirmOrder(any(RequestTossConfirmDTO.class), anyString()))
			.thenReturn(responseEntity);

		// when
		ResponseEntity<ResponseTossPaymentConfirmDTO> response =
			orderService.confirmOrder(orderId, paymentKey, amount);

		// then
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		verify(orderJpaRepository, never()).save(any());
	}

	@Test
	@DisplayName("주문서 삭제 요청 성공")
	void testCancelOrder_Success() {
		// given
		String orderId = "TEST-ORDER-CODE";

		// when
		ResponseEntity<Void> response = orderService.cancelOrder(orderId);

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
		Order orderEntity = mock(Order.class);
		ResponseOrderDTO responseOrderDTO = new ResponseOrderDTO();
		responseOrderDTO.setCustomerId(customerId);

		when(orderJpaRepository.findById(orderCode))
			.thenReturn(Optional.of(orderEntity));
		// map to DTO
		Mockito.mockStatic(ResponseOrderDTO.class)
			.when(() -> ResponseOrderDTO.fromEntity(orderEntity))
			.thenReturn(responseOrderDTO);

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
		when(orderDetailJpaRepository.findByOrderOrderCode(orderCode))
			.thenReturn(orderDetailList);

		ResponseOrderDetailDTO orderDetailDTO = new ResponseOrderDetailDTO();
		Mockito.mockStatic(ResponseOrderDetailDTO.class)
			.when(() -> ResponseOrderDetailDTO.fromEntity(orderDetail))
			.thenReturn(orderDetailDTO);

		// when
		ResponseOrderWrapperDTO result = orderService.getOrderByOrderCode(orderCode);

		// then
		assertNotNull(result);
		assertEquals(responseOrderDTO, result.getOrder());
		assertEquals(1, result.getOrderDetails().size());
	}
}
