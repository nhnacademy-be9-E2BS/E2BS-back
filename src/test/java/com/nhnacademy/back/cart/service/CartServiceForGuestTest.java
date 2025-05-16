package com.nhnacademy.back.cart.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.cart.domain.dto.CartDTO;
import com.nhnacademy.back.cart.domain.dto.CartItemDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForGuestDTO;
import com.nhnacademy.back.cart.service.impl.CartServiceImpl;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@ExtendWith(MockitoExtension.class)
class CartServiceForGuestTest {

	@Mock
	private ProductJpaRepository productRepository;

	@Mock
	private ProductCategoryJpaRepository productCategoryRepository;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ObjectMapper objectMapper;

	/// redisTemplate.opsForValue()에서 사용되는 구현체
	@Mock
	private ValueOperations<String, Object> valueOperations;

	@InjectMocks
	private CartServiceImpl cartService;


	private final String sessionId = "guest-session-123";
	private Product product;


	@BeforeEach
	void setUp() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		product = new Product(1L, new ProductState(ProductStateName.SALE), new Publisher("a"),
			"title1", "content1", "description", LocalDate.now(), "isbn",
			10000, 8000, false, 1, 0, 0, null);
	}


	@Test
	@DisplayName("게스트 장바구니 항목 추가 테스트")
	void createCartItemForGuest() {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(null, sessionId, 1L, 2);

		CartDTO cart = new CartDTO();

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productCategoryRepository.findByProduct_ProductId(1L)).thenReturn(List.of());
		when(redisTemplate.opsForValue().get(sessionId)).thenReturn(Object.class);
		when(objectMapper.convertValue(Object.class, CartDTO.class)).thenReturn(cart);

		// when
		cartService.createCartItemForGuest(request);

		// then
		verify(redisTemplate.opsForValue()).set(eq(sessionId), any(CartDTO.class));
	}

	@Test
	@DisplayName("게스트 장바구니 항목 수량 변경 테스트")
	void updateCartItemForGuest() {
		// given
		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO(sessionId, 1L, 5);

		CartItemDTO cartItem = new CartItemDTO(1L, List.of(), "Product", 1000, "img.jpg", 2);
		CartDTO cart = new CartDTO(List.of(cartItem));

		when(redisTemplate.opsForValue().get(sessionId)).thenReturn(cart);
		when(objectMapper.convertValue(cart, CartDTO.class)).thenReturn(cart);

		// when
		cartService.updateCartItemForGuest(request);

		// then
		assertThat(cartItem.getCartItemsQuantity()).isEqualTo(5);
		verify(redisTemplate.opsForValue()).set(eq(sessionId), any(CartDTO.class));
	}

	@Test
	@DisplayName("게스트 장바구니 항목 삭제 테스트")
	void deleteCartItemForGuest_shouldRemoveItem() {
		// given
		CartItemDTO item = new CartItemDTO(1L, List.of(), "Product", 1000, "img.jpg", 2);
		CartDTO cart = new CartDTO(new ArrayList<>(List.of(item)));
		RequestDeleteCartItemsForGuestDTO request = new RequestDeleteCartItemsForGuestDTO(1L, sessionId);

		when(redisTemplate.opsForValue().get(sessionId)).thenReturn(cart);
		when(objectMapper.convertValue(cart, CartDTO.class)).thenReturn(cart);

		// when
		cartService.deleteCartItemForGuest(request);

		// then
		assertEquals(0, cart.getCartItems().size());
		verify(redisTemplate.opsForValue()).set(eq(sessionId), any(CartDTO.class));
	}

	@Test
	@DisplayName("게스트 장바구니 항목 전체 삭제 테스트")
	void deleteCartForGuest() {
		// given
		CartDTO cart = new CartDTO();
		when(redisTemplate.opsForValue().get(sessionId)).thenReturn(cart);
		when(objectMapper.convertValue(cart, CartDTO.class)).thenReturn(cart);

		// when
		cartService.deleteCartForGuest(sessionId);

		// then
		verify(redisTemplate).delete(sessionId);
	}

	@Test
	@DisplayName("게스트 장바구니 목록 조회 테스트")
	void getCartItemsByGuest() {
		// given
		CartItemDTO item1 = new CartItemDTO(1L, List.of(), "Product1", 1000, "img1.jpg", 1);
		CartItemDTO item2 = new CartItemDTO(2L, List.of(), "Product2", 2000, "img2.jpg", 2);
		CartDTO cart = new CartDTO(List.of(item1, item2));

		when(redisTemplate.opsForValue().get(sessionId)).thenReturn(cart);
		when(objectMapper.convertValue(cart, CartDTO.class)).thenReturn(cart);

		// when
		List<ResponseCartItemsForGuestDTO> result = cartService.getCartItemsByGuest(sessionId);

		// then
		assertEquals(2, result.size());
	}

}
