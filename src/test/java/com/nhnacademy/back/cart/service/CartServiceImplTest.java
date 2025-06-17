package com.nhnacademy.back.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.cart.domain.dto.CartDTO;
import com.nhnacademy.back.cart.domain.dto.CartItemDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartOrderDTO;
import com.nhnacademy.back.cart.service.impl.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ObjectMapper objectMapper;

	/// redisTemplate.opsForValue()에서 사용되는 구현체
	@Mock
	private ValueOperations<String, Object> valueOperations;
	/// redisTemplate.opsForHash()에서 사용되는 구현체
	@Mock
	private HashOperations<String, Object, Object> hashOperations;

	@InjectMocks
	private CartServiceImpl cartService;


	private final String memberId = "member123";
	private final String sessionId = "guest-session-xyz";


	@Test
	@DisplayName("게스트 장바구니를 회원 장바구니에 병합 테스트")
	void mergeCartItemsToMemberFromGuest() {
		// given
		CartItemDTO guestItemDTO = new CartItemDTO(100L, "productA", 2000, 1000, new BigDecimal(50), "image", 2);
		CartDTO guestCartDTO = new CartDTO();
		guestCartDTO.setCartItems(new ArrayList<>(List.of(guestItemDTO)));

		CartDTO memberCartDTO = new CartDTO();
		memberCartDTO.setCartItems(new ArrayList<>());

		String redisGuestKey = "GuestCart:" + sessionId;

		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.hasKey("MemberCart:", memberId)).thenReturn(false);
		doNothing().when(hashOperations).put(eq("MemberCart:"), eq(memberId), any(CartDTO.class));

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(redisGuestKey)).thenReturn(guestCartDTO);

		when(objectMapper.convertValue(guestCartDTO, CartDTO.class)).thenReturn(guestCartDTO);
		when(redisTemplate.delete(redisGuestKey)).thenReturn(true);

		// when
		Integer result = cartService.mergeCartItemsToMemberFromGuest(memberId, sessionId);

		// then
		assertEquals(1, result); // guest 장바구니의 1개 항목이 병합됨
		verify(redisTemplate).delete(redisGuestKey);
		verify(hashOperations).put(eq("MemberCart:"), eq(memberId), any(CartDTO.class));
	}

	@Test
	@DisplayName("게스트 장바구니를 회원 장바구니에 병합 테스트 - 회원 장바구니에 동일 상품 존재 시 수량 증가 테스트")
	void mergeCartItemsToMemberFromGuest_IncreaseQuantity() {
		// given
		long productId = 100L;
		int originalQuantity = 3;
		int guestQuantity = 2;

		// 회원 장바구니에 이미 존재하는 상품
		CartItemDTO memberItemDTO = new CartItemDTO(productId, "productA", 2000, 1000, new BigDecimal(50), "image", originalQuantity);
		CartDTO memberCartDTO = new CartDTO();
		memberCartDTO.setCartItems(new ArrayList<>(List.of(memberItemDTO)));

		// 게스트 장바구니에 동일한 상품 (수량 2)
		CartItemDTO guestItemDTO = new CartItemDTO(productId, "productA", 2000, 1000, new BigDecimal(50), "image", guestQuantity);
		CartDTO guestCartDTO = new CartDTO();
		guestCartDTO.setCartItems(new ArrayList<>(List.of(guestItemDTO)));

		String redisGuestKey = "GuestCart:" + sessionId;

		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.hasKey("MemberCart:", memberId)).thenReturn(true);
		when(hashOperations.get("MemberCart:", memberId)).thenReturn(memberCartDTO);
		when(objectMapper.convertValue(memberCartDTO, CartDTO.class)).thenReturn(memberCartDTO);
		doNothing().when(hashOperations).put(eq("MemberCart:"), eq(memberId), any(CartDTO.class));

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(redisGuestKey)).thenReturn(guestCartDTO);
		when(objectMapper.convertValue(guestCartDTO, CartDTO.class)).thenReturn(guestCartDTO);

		when(redisTemplate.delete(redisGuestKey)).thenReturn(true);

		// when
		Integer result = cartService.mergeCartItemsToMemberFromGuest(memberId, sessionId);

		// then
		assertEquals(1, result); // 병합 후 1개 상품 존재
		assertEquals(originalQuantity + guestQuantity, memberItemDTO.getCartItemsQuantity()); // 수량이 5로 증가
		verify(redisTemplate).delete(redisGuestKey);
		verify(hashOperations).put(eq("MemberCart:"), eq(memberId), any(CartDTO.class));
	}

	@Test
	@DisplayName("게스트 장바구니를 회원 장바구니에 병합 테스트 - 게스트 장바구니가 존재하지 않는 경우")
	void mergeCartItemsToMemberFromGuest_GuestCartNull() {
		// given
		CartItemDTO memberItemDTO = new CartItemDTO(100L, "productA", 2000, 1000, new BigDecimal(50), "image", 3);
		CartDTO memberCartDTO = new CartDTO();
		memberCartDTO.setCartItems(new ArrayList<>(List.of(memberItemDTO)));

		String redisGuestKey = "GuestCart:" + sessionId;

		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.hasKey("MemberCart:", memberId)).thenReturn(true);
		when(hashOperations.get("MemberCart:", memberId)).thenReturn(memberCartDTO);
		when(objectMapper.convertValue(memberCartDTO, CartDTO.class)).thenReturn(memberCartDTO);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(redisGuestKey)).thenReturn(null); // 게스트 장바구니 없음

		// when
		Integer result = cartService.mergeCartItemsToMemberFromGuest(memberId, sessionId);

		// then
		assertEquals(1, result);
		verify(redisTemplate, never()).delete(redisGuestKey);
		verify(hashOperations, never()).put(any(), any(), any());
	}

	@Test
	@DisplayName("주문 완료한 상품 항목 장바구니에 수량 변경 또는 삭제 업데이트 테스트 - 회원")
	void deleteOrderCompleteCartItems_member() {
		// given
		String memberId = "member1";
		RequestDeleteCartOrderDTO dto = new RequestDeleteCartOrderDTO();
		dto.setMemberId(memberId);
		dto.setProductIds(List.of(100L));

		CartItemDTO item1 = new CartItemDTO(100L, "productA", 2000, 1000, new BigDecimal(50), "img", 1);
		CartItemDTO item2 = new CartItemDTO(200L, "productB", 2000, 1000, new BigDecimal(50), "img", 1);
		CartDTO cartDTO = new CartDTO(new ArrayList<>(List.of(item1, item2)));

		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.get("MemberCart:", memberId)).thenReturn(cartDTO);
		when(objectMapper.convertValue(cartDTO, CartDTO.class)).thenReturn(cartDTO);

		// when
		Integer result = cartService.deleteOrderCompleteCartItems(dto);

		// then
		assertEquals(1, result);
		verify(hashOperations).put(eq("MemberCart:"), eq(memberId), any(CartDTO.class));
	}

	@Test
	@DisplayName("주문 완료한 상품 항목 장바구니에 수량 변경 또는 삭제 업데이트 테스트 - 게스트")
	void deleteOrderCompleteCartItems_guest() {
		// given
		RequestDeleteCartOrderDTO dto = new RequestDeleteCartOrderDTO("", sessionId, List.of(300L), List.of(1));

		CartItemDTO item1 = new CartItemDTO(300L, "productC", 2000, 1000, new BigDecimal(50), "img", 1);
		CartItemDTO item2 = new CartItemDTO(400L, "productD", 2000, 1000, new BigDecimal(50), "img", 1);
		CartDTO cartDTO = new CartDTO(new ArrayList<>(List.of(item1, item2)));

		String redisKey = "GuestCart:" + sessionId;
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(redisKey)).thenReturn(cartDTO);
		when(objectMapper.convertValue(cartDTO, CartDTO.class)).thenReturn(cartDTO);

		// when
		Integer result = cartService.deleteOrderCompleteCartItems(dto);

		// then
		assertEquals(1, result);
		verify(valueOperations).set(eq(redisKey), any(CartDTO.class));
	}

	@Test
	@DisplayName("주문 완료한 상품 항목 장바구니에 수량 변경 또는 삭제 업데이트 테스트 - 회원 장바구니가 비어 있는 경우")
	void deleteOrderCompleteCartItems_member_emptyCart() {
		// given
		RequestDeleteCartOrderDTO dto = new RequestDeleteCartOrderDTO(memberId, "", List.of(1L), List.of(1));

		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.get("MemberCart:", memberId)).thenReturn(null);
		when(objectMapper.convertValue(null, CartDTO.class)).thenReturn(null);

		// when
		Integer result = cartService.deleteOrderCompleteCartItems(dto);

		// then
		assertEquals(0, result);
	}

	@Test
	@DisplayName("주문 완료한 상품 항목 장바구니에 수량 변경 또는 삭제 업데이트 테스트 - 게스트 장바구니가 비어 있는 경우")
	void deleteOrderCompleteCartItems_guest_emptyCart() {
		// given
		RequestDeleteCartOrderDTO dto = new RequestDeleteCartOrderDTO("", sessionId, List.of(1L), List.of(1));

		String redisKey = "GuestCart:" + sessionId;

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(redisKey)).thenReturn(null);
		when(objectMapper.convertValue(null, CartDTO.class)).thenReturn(null);

		// when
		Integer result = cartService.deleteOrderCompleteCartItems(dto);

		// then
		assertEquals(0, result);
	}

}
