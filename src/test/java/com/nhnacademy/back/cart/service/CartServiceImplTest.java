package com.nhnacademy.back.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.cart.domain.dto.CartDTO;
import com.nhnacademy.back.cart.domain.dto.CartItemDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartOrderDTO;
import com.nhnacademy.back.cart.domain.entity.Cart;
import com.nhnacademy.back.cart.domain.entity.CartItems;
import com.nhnacademy.back.cart.exception.CartNotFoundException;
import com.nhnacademy.back.cart.repository.CartItemsJpaRepository;
import com.nhnacademy.back.cart.repository.CartJpaRepository;
import com.nhnacademy.back.cart.service.impl.CartServiceImpl;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

	@Mock
	private CustomerJpaRepository customerRepository;

	@Mock
	private MemberJpaRepository memberRepository;

	@Mock
	private ProductJpaRepository productRepository;

	@Mock
	private CartJpaRepository cartRepository;

	@Mock
	private CartItemsJpaRepository cartItemsRepository;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ObjectMapper objectMapper;

	/// redisTemplate.opsForValue()에서 사용되는 구현체
	@Mock
	private ValueOperations<String, Object> valueOperations;

	@InjectMocks
	private CartServiceImpl cartService;

	private final String memberId = "member123";
	private final String sessionId = "guest-session-xyz";

	@Test
	@DisplayName("게스트 장바구니를 회원 장바구니에 병합 테스트")
	void mergeCartItemsToMemberFromGuest() {
		// given
		Customer customer = new Customer(1L, "email", "pwd", "홍길동");
		
		Member member = Member.builder()
			.customerId(1L)
			.customer(customer)
			.build();

		Cart cart = new Cart(customer);

		CartDTO redisCartDTO = new CartDTO();
		CartItemDTO itemDTO = new CartItemDTO(100L, "productA", 2000, 1000, new BigDecimal(50), "image", 2);
		redisCartDTO.setCartItems(List.of(itemDTO));

		Product product = Product.builder()
			.build();

		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		when(cartRepository.existsByCustomer_CustomerId(1L)).thenReturn(false);
		when(cartRepository.save(any())).thenReturn(cart);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(sessionId)).thenReturn(redisCartDTO);
		when(objectMapper.convertValue(redisCartDTO, CartDTO.class)).thenReturn(redisCartDTO);

		when(productRepository.findById(100L)).thenReturn(Optional.of(product));
		when(cartItemsRepository.existsByCartAndProduct(cart, product)).thenReturn(false);

		// when
		Integer result = cartService.mergeCartItemsToMemberFromGuest(memberId, sessionId);

		// then
		assertEquals(0, result);
		verify(cartItemsRepository).save(any());
	}

	@Test
	@DisplayName("게스트 장바구니를 회원 장바구니에 병합 테스트 - 수량 증가")
	void mergeCartItemsToMemberFromGuest_UpdateQuantity() {
		// given
		Customer customer = new Customer(1L, "email", "pwd", "홍길동");

		Member member = Member.builder()
			.customerId(1L)
			.customer(customer)
			.build();

		Cart cart = new Cart(customer);

		CartDTO redisCartDTO = new CartDTO();
		CartItemDTO itemDTO = new CartItemDTO(200L, "productA", 2000, 1000, new BigDecimal(50), "image", 3);
		redisCartDTO.setCartItems(List.of(itemDTO));

		Product product = Product.builder()
			.build();

		CartItems existingCartItem = new CartItems(cart, product, 1); // 기존 수량 1

		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		when(cartRepository.existsByCustomer_CustomerId(1L)).thenReturn(true);
		when(cartRepository.findByCustomer_CustomerId(1L)).thenReturn(Optional.of(cart));

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(sessionId)).thenReturn(redisCartDTO);
		when(objectMapper.convertValue(redisCartDTO, CartDTO.class)).thenReturn(redisCartDTO);

		when(productRepository.findById(200L)).thenReturn(Optional.of(product));
		when(cartItemsRepository.existsByCartAndProduct(cart, product)).thenReturn(true);
		when(cartItemsRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingCartItem));

		// when
		Integer result = cartService.mergeCartItemsToMemberFromGuest(memberId, sessionId);

		// then
		assertEquals(0, result);
		assertEquals(4, existingCartItem.getCartItemsQuantity());
	}

	@Test
	@DisplayName("게스트 장바구니를 회원 장바구니에 병합 테스트 - 실패(회원이 없는 경우)")
	void mergeCartItemsToMemberFromGuest_Fail_NotFoundMember() {
		// given
		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(null);

		// when & then
		assertThrows(NotFoundMemberException.class, () ->
			cartService.mergeCartItemsToMemberFromGuest(memberId, sessionId));
	}

	@Test
	@DisplayName("게스트 장바구니를 회원 장바구니에 병합 테스트 - 실패(Customer 없는 경우)")
	void mergeCartItemsToMemberFromGuest_Fail_NotFoundCustomer() {
		// given
		Member member = Member.builder()
			.customerId(1L)
			.build();

		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(customerRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThrows(CustomerNotFoundException.class, () ->
			cartService.mergeCartItemsToMemberFromGuest(memberId, sessionId));
	}

	@Test
	@DisplayName("게스트 장바구니를 회원 장바구니에 병합 테스트 - 실패(게스트 장바구니가 없는 경우)")
	void mergeCartItemsToMemberFromGuest_Fail_NotFoundGuestCart() {
		// given
		Customer customer = new Customer(1L, "email", "pwd", "홍길동");

		Member member = Member.builder()
			.customerId(1L)
			.customer(customer)
			.build();

		Cart cart = new Cart(customer);

		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		when(cartRepository.existsByCustomer_CustomerId(1L)).thenReturn(true);
		when(cartRepository.findByCustomer_CustomerId(1L)).thenReturn(Optional.of(cart));

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(sessionId)).thenReturn(null);

		// when
		Integer result = cartService.mergeCartItemsToMemberFromGuest(memberId, sessionId);

		// then
		assertEquals(0, result);
	}

	@Test
	@DisplayName("주문 완료한 상품을 장바구니에 비워주기 테스트 - 회원")
	void deleteOrderCompleteCartItems_Member() {
		// given
		Customer customer = new Customer(1L, "email", "pwd", "홍길동");

		String memberId = "member123";
		List<Long> productIds = List.of(1L);
		List<Integer> quantities = List.of(2);

		RequestDeleteCartOrderDTO dto = new RequestDeleteCartOrderDTO(memberId, null, productIds, quantities);

		Member member = Member.builder()
			.customerId(100L)
			.customer(customer)
			.memberId(memberId)
			.build();

		Product product = Product.builder()
			.productId(1L)
			.build();

		Cart cart = new Cart(customer);
		CartItems item1 = new CartItems(cart, product, 5);
		CartItems item2 = new CartItems(cart, product, 3);
		cart.getCartItems().add(item1);
		cart.getCartItems().add(item2);

		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(cartRepository.findByCustomer_CustomerId(member.getCustomerId())).thenReturn(Optional.of(cart));

		// when
		Integer result = cartService.deleteOrderCompleteCartItems(dto);

		// then
		assertEquals(1, result); // item1만 남음
		verify(cartItemsRepository, times(1)).delete(item2);
	}

	@Test
	@DisplayName("주문 완료한 상품을 장바구니에 비워주기 테스트 - 비회원")
	void deleteOrderCompleteCartItems_Guest() {
		// given
		String sessionId = "guest-session";
		List<Long> productIds = List.of(1L);
		
		CartItemDTO item1 = new CartItemDTO(1L, "A", 1000, 1000, new BigDecimal(0), "image", 2);
		CartItemDTO item2 = new CartItemDTO(2L, "A", 1000, 1000, new BigDecimal(0), "image", 1);

		CartDTO cartDTO = new CartDTO();
		cartDTO.setCartItems(new ArrayList<>(List.of(item1, item2)));

		RequestDeleteCartOrderDTO dto = new RequestDeleteCartOrderDTO(null, sessionId, productIds, List.of(2));

		ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOps);
		when(valueOps.get(sessionId)).thenReturn(cartDTO);
		when(objectMapper.convertValue(cartDTO, CartDTO.class)).thenReturn(cartDTO);

		// when
		Integer result = cartService.deleteOrderCompleteCartItems(dto);

		// then
		assertEquals(1, result); // item1 삭제됨
	}

	@Test
	@DisplayName("주문 완료한 상품을 장바구니에 비워주기 테스트 - 실패(회원을 찾지 못한 경우)")
	void deleteOrderCompleteCartItems_NotFoundMember() {
		// given
		RequestDeleteCartOrderDTO dto = new RequestDeleteCartOrderDTO("member123", null, List.of(1L), List.of(1));
		when(memberRepository.getMemberByMemberId("member123")).thenReturn(null);

		// when & then
		assertThrows(NotFoundMemberException.class, () -> cartService.deleteOrderCompleteCartItems(dto));
	}

	@Test
	@DisplayName("주문 완료한 상품을 장바구니에 비워주기 테스트 - 실패(회원 장바구니를 찾지 못한 경우)")
	void deleteOrderCompleteCartItems_NotFoundMemberCart() {
		// given
		Member member = Member.builder()
			.customerId(10L)
			.memberId("member123")
			.build();

		RequestDeleteCartOrderDTO dto = new RequestDeleteCartOrderDTO("member123", null, List.of(1L), List.of(1));

		when(memberRepository.getMemberByMemberId("member123")).thenReturn(member);
		when(cartRepository.findByCustomer_CustomerId(member.getCustomerId())).thenReturn(Optional.empty());

		// when & then
		assertThrows(CartNotFoundException.class, () -> cartService.deleteOrderCompleteCartItems(dto));
	}

}
