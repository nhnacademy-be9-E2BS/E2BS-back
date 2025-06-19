package com.nhnacademy.back.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.cart.domain.dto.CartDTO;
import com.nhnacademy.back.cart.domain.dto.CartItemDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForMemberDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForMemberDTO;
import com.nhnacademy.back.cart.exception.CartAlreadyExistsException;
import com.nhnacademy.back.cart.service.impl.CartServiceImpl;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotForSaleException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@ExtendWith(MockitoExtension.class)
class CartServiceImplForMemberTest {

	@Mock
	private ProductJpaRepository productRepository;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ObjectMapper objectMapper;

	/// redisTemplate.opsForHash()에서 사용되는 구현체
	@Mock
	private HashOperations<String, Object, Object> hashOperations;

	@InjectMocks
	private CartServiceImpl cartService;


	private final String memberHashName = "MemberCart:";
	private final String memberId = "member1";
	private final Long productId = 1L;

	@BeforeEach
	void setup() {
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
	}


	@Test
	@DisplayName("회원 장바구니 생성 테스트")
	void createCartForMember() {
		// given
		when(hashOperations.hasKey(memberHashName, memberId)).thenReturn(false);

		// when
		cartService.createCartForMember(memberId);

		// then
		verify(hashOperations).put(eq(memberHashName), eq(memberId), any(CartDTO.class));
	}

	@Test
	@DisplayName("회원 장바구니 생성 테스트 - 실패(장바구니 이미 존재한 경우)")
	void createCartForMember_alreadyExists_throwException() {
		// given
		when(hashOperations.hasKey(memberHashName, memberId)).thenReturn(true);

		// when & then
		assertThrows(CartAlreadyExistsException.class, () -> cartService.createCartForMember(memberId));
		verify(hashOperations, never()).put(any(), any(), any());
	}


	@Test
	@DisplayName("회원 장바구니 항목 생성 테스트 - 신규 상품 추가")
	void createCartItemForMember_newItem() {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(memberId, "", productId, 2);

		Product product = Product.builder()
			.productId(productId)
			.productState(new ProductState(1L, ProductStateName.SALE))
			.productStock(3)
			.build();

		when(hashOperations.hasKey(memberHashName, memberId)).thenReturn(true);
		when(hashOperations.get(memberHashName, memberId)).thenReturn(new CartDTO());
		when(objectMapper.convertValue(any(), eq(CartDTO.class))).thenReturn(new CartDTO());

		when(productRepository.findById(productId)).thenReturn(Optional.of(product));

		// when
		int size = cartService.createCartItemForMember(request);

		// then
		assertEquals(1, size);
		verify(hashOperations).put(eq(memberHashName), eq(memberId), any(CartDTO.class));
	}

	@Test
	@DisplayName("회원 장바구니 항목 생성 테스트 - 기존 상품 수량 증가")
	void createCartItemForMember_existingItem() {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(memberId, "", productId, 3);

		Product product = Product.builder()
			.productId(productId)
			.productState(new ProductState(1L, ProductStateName.SALE))
			.productStock(3)
			.build();

		CartItemDTO existingItem = new CartItemDTO(productId, "title", 1000, 800, BigDecimal.valueOf(20), "image", 2);
		CartDTO cartDTO = new CartDTO();
		cartDTO.setCartItems(new ArrayList<>(List.of(existingItem)));

		when(hashOperations.hasKey(memberHashName, memberId)).thenReturn(true);
		when(hashOperations.get(memberHashName, memberId)).thenReturn(cartDTO);
		when(objectMapper.convertValue(any(), eq(CartDTO.class))).thenReturn(cartDTO);

		when(productRepository.findById(productId)).thenReturn(Optional.of(product));

		// when
		int size = cartService.createCartItemForMember(request);

		// then
		assertEquals(1, size);
		assertEquals(5, existingItem.getCartItemsQuantity()); // 2 + 3 = 5
		verify(hashOperations).put(memberHashName, memberId, cartDTO);
	}

	@Test
	@DisplayName("회원 장바구니 항목 생성 테스트 - 실패(판매중이지 않은 상품인 경우)")
	void createCartItemForMember_productNotForSale() {
		// given
		Product product = Product.builder()
			.productId(productId)
			.productState(new ProductState(2L, ProductStateName.OUT))
			.build();

		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(memberId, "", productId, 1);

		when(hashOperations.hasKey(memberHashName, memberId)).thenReturn(false);
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));

		// when & then
		assertThrows(ProductNotForSaleException.class, () -> cartService.createCartItemForMember(request));
	}

	@Test
	@DisplayName("회원 장바구니 항목 수량 수정 테스트")
	void updateCartItemForMember() {
		// given
		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO(memberId, "", productId, 5);

		CartItemDTO cartItem = new CartItemDTO(productId, "title", 1000, 900, BigDecimal.ZERO, "image", 2);
		CartDTO cartDTO = new CartDTO();
		cartDTO.setCartItems(new ArrayList<>(List.of(cartItem)));

		when(hashOperations.get(memberHashName, memberId)).thenReturn(cartDTO);
		when(objectMapper.convertValue(any(), eq(CartDTO.class))).thenReturn(cartDTO);

		// when
		int size = cartService.updateCartItemForMember(request);

		// then
		assertEquals(1, size);
		assertEquals(5, cartItem.getCartItemsQuantity());
		verify(hashOperations).put(memberHashName, memberId, cartDTO);
	}

	@Test
	@DisplayName("회원 장바구니 항목 수량 수정 테스트 - 수량 0이면 삭제")
	void updateCartItemForMember_quantityZero_delete() {
		// given
		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO(memberId, "", productId, 0);

		CartItemDTO cartItem = new CartItemDTO(productId, "title", 1000, 900, BigDecimal.ZERO, "image", 2);
		CartDTO cartDTO = new CartDTO();
		cartDTO.setCartItems(new ArrayList<>(List.of(cartItem)));

		when(hashOperations.get(memberHashName, memberId)).thenReturn(cartDTO);
		when(objectMapper.convertValue(any(), eq(CartDTO.class))).thenReturn(cartDTO);

		// when
		int size = cartService.updateCartItemForMember(request);

		// then
		assertEquals(0, size);
		verify(hashOperations).put(memberHashName, memberId, cartDTO);
	}

	@Test
	@DisplayName("회원 장바구니 항목 삭제 테스트")
	void deleteCartItemForMember() {
		// given
		RequestDeleteCartItemsForMemberDTO request = new RequestDeleteCartItemsForMemberDTO(memberId, productId);

		CartItemDTO cartItem = new CartItemDTO(productId, "title", 1000, 900, BigDecimal.ZERO, "image", 1);
		CartDTO cartDTO = new CartDTO();
		cartDTO.setCartItems(new ArrayList<>(List.of(cartItem)));

		when(hashOperations.get(memberHashName, memberId)).thenReturn(cartDTO);
		when(objectMapper.convertValue(any(), eq(CartDTO.class))).thenReturn(cartDTO);

		// when
		cartService.deleteCartItemForMember(request);

		// then
		assertTrue(cartDTO.getCartItems().isEmpty());
		verify(hashOperations).put(memberHashName, memberId, cartDTO);
	}

	@Test
	@DisplayName("회원 장바구니 전체 삭제 테스트")
	void deleteCartForMember_success() {
		// given
		cartService.deleteCartForMember(memberId);

		// when & then
		verify(hashOperations).put(eq(memberHashName), eq(memberId), any(CartDTO.class));
	}

	@Test
	@DisplayName("회원 장바구니 목록 조회 테스트")
	void getCartItemsByMember() {
		// given
		Product product = Product.builder()
			.productId(productId)
			.build();

		CartItemDTO cartItem = new CartItemDTO(productId, "title", 1000, 900, BigDecimal.ZERO, "image", 3);
		CartDTO cartDTO = new CartDTO();
		cartDTO.setCartItems(new ArrayList<>(List.of(cartItem)));

		when(hashOperations.get(memberHashName, memberId)).thenReturn(cartDTO);
		when(objectMapper.convertValue(any(), eq(CartDTO.class))).thenReturn(cartDTO);

		when(productRepository.findById(productId)).thenReturn(Optional.of(product));

		// when
		List<ResponseCartItemsForMemberDTO> responses = cartService.getCartItemsByMember(memberId);

		// then
		assertFalse(responses.isEmpty());
		verify(productRepository).findById(productId);
	}

	@Test
	@DisplayName("회원 장바구니 목록 조회 - 장바구니가 비었을 경우")
	void getCartItemsByMember_emptyCart() {
		// given
		when(hashOperations.get(memberHashName, memberId)).thenReturn(null);
		when(objectMapper.convertValue(any(), eq(CartDTO.class))).thenReturn(null);

		// when
		List<ResponseCartItemsForMemberDTO> responses = cartService.getCartItemsByMember(memberId);

		// then
		assertTrue(responses.isEmpty());
	}

}
