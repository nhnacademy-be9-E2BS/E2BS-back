package com.nhnacademy.back.order.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.order.order.model.dto.response.ResponseOrderReturnDTO;
import com.nhnacademy.back.order.order.model.entity.Order;
import com.nhnacademy.back.order.orderreturn.domain.entity.OrderReturn;
import com.nhnacademy.back.order.orderreturn.domain.entity.ReturnCategory;
import com.nhnacademy.back.order.orderreturn.repository.OrderReturnJpaRepository;
import com.nhnacademy.back.order.orderreturn.service.impl.OrderReturnServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderReturnServiceImplTest {

	@InjectMocks
	private OrderReturnServiceImpl orderReturnService;

	@Mock
	private MemberJpaRepository memberJpaRepository;

	@Mock
	private OrderReturnJpaRepository orderReturnJpaRepository;

	@Test
	@DisplayName("회원의 반품 목록 조회")
	void testGetOrderReturnsByMemberId() {
		String memberId = "user123";
		Pageable pageable = PageRequest.of(0, 5);

		Member member = mock(Member.class);
		Customer customer = mock(Customer.class);
		when(member.getCustomer()).thenReturn(customer);
		when(memberJpaRepository.getMemberByMemberId(memberId)).thenReturn(member);

		Order order = mock(Order.class);
		OrderReturn orderReturn = mock(OrderReturn.class);
		ReturnCategory returnCategory = ReturnCategory.BREAK;
		Page<OrderReturn> page = new PageImpl<>(List.of(orderReturn));
		when(orderReturnJpaRepository.findByOrder_CustomerOrderByOrderReturnCreatedAtDesc(customer,
			pageable)).thenReturn(page);
		when(orderReturn.getOrderReturnId()).thenReturn(1L);
		when(orderReturn.getOrder()).thenReturn(order);
		when(order.getOrderCode()).thenReturn("TEST-ORDER-CODE");
		when(orderReturn.getOrderReturnReason()).thenReturn("책이 찢어짐");
		when(orderReturn.getOrderReturnAmount()).thenReturn(1L);
		when(orderReturn.getOrderReturnCreatedAt()).thenReturn(LocalDateTime.now());
		when(orderReturn.getReturnCategory()).thenReturn(returnCategory);

		ResponseEntity<Page<ResponseOrderReturnDTO>> response = orderReturnService.getOrderReturnsByMemberId(memberId,
			pageable);

		assertNotNull(response.getBody());
		assertTrue(response.getStatusCode().is2xxSuccessful());
		verify(memberJpaRepository).getMemberByMemberId(memberId);
	}

	@Test
	@DisplayName("반품 상세 내역 조회")
	void testGetOrderReturnByOrderCode() {
		String orderCode = "TEST-ORDER-CODE";
		Order order = mock(Order.class);
		OrderReturn orderReturn = mock(OrderReturn.class);
		ReturnCategory returnCategory = ReturnCategory.BREAK;
		when(orderReturnJpaRepository.findByOrder_OrderCode(orderCode)).thenReturn(Optional.of(orderReturn));

		when(orderReturn.getOrderReturnId()).thenReturn(1L);
		when(orderReturn.getOrder()).thenReturn(order);
		when(order.getOrderCode()).thenReturn(orderCode);
		when(orderReturn.getOrderReturnReason()).thenReturn("단순 변심");
		when(orderReturn.getOrderReturnAmount()).thenReturn(1L);
		when(orderReturn.getOrderReturnCreatedAt()).thenReturn(LocalDateTime.now());
		when(orderReturn.getReturnCategory()).thenReturn(returnCategory);

		ResponseEntity<ResponseOrderReturnDTO> response = orderReturnService.getOrderReturnByOrderCode(orderCode);

		assertTrue(response.getStatusCode().is2xxSuccessful());
		assertNotNull(response.getBody());
		verify(orderReturnJpaRepository).findByOrder_OrderCode(orderCode);
	}

	@Test
	@DisplayName("관리자의 전체 반품 항목 조회")
	void testGetOrderReturnsAll() {
		Pageable pageable = PageRequest.of(0, 10);
		Order order = mock(Order.class);
		OrderReturn orderReturn = mock(OrderReturn.class);
		ReturnCategory returnCategory = ReturnCategory.BREAK;
		Page<OrderReturn> page = new PageImpl<>(List.of(orderReturn));
		when(orderReturnJpaRepository.findAllByOrderByOrderReturnCreatedAtDesc(pageable)).thenReturn(page);

		when(orderReturn.getOrderReturnId()).thenReturn(1L);
		when(orderReturn.getOrder()).thenReturn(order);
		when(order.getOrderCode()).thenReturn("TEST-ORDER-CODE");
		when(orderReturn.getOrderReturnReason()).thenReturn("단순 변심");
		when(orderReturn.getOrderReturnAmount()).thenReturn(1L);
		when(orderReturn.getOrderReturnCreatedAt()).thenReturn(LocalDateTime.now());
		when(orderReturn.getReturnCategory()).thenReturn(returnCategory);

		ResponseEntity<Page<ResponseOrderReturnDTO>> response = orderReturnService.getOrderReturnsAll(pageable);

		assertTrue(response.getStatusCode().is2xxSuccessful());
		assertEquals(1, response.getBody().getContent().size());
		verify(orderReturnJpaRepository).findAllByOrderByOrderReturnCreatedAtDesc(pageable);
	}
}
