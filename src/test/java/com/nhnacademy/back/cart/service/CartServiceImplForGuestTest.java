package com.nhnacademy.back.cart.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Duration;
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
import com.nhnacademy.back.common.util.MinioUtils;
import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotForSaleException;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@ExtendWith(MockitoExtension.class)
class CartServiceImplForGuestTest {

	@Mock
	private ProductJpaRepository productRepository;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ObjectMapper objectMapper;

	/// redisTemplate.opsForValue()에서 사용되는 구현체
	@Mock
	private ValueOperations<String, Object> valueOperations;

	@Mock
	private MinioUtils minioUtils;

	@InjectMocks
	private CartServiceImpl cartService;


	private final String hashName = "GuestCart:";
	private Product product;

	@BeforeEach
	void setUp() {
		product = new Product(1L, new ProductState(1L, ProductStateName.SALE), new Publisher("a"),
			"title1", "content1", "description", LocalDate.now(), "isbn",
			10000, 8000, false, 1, new ArrayList<>());
	}

	@Test
	@DisplayName("장바구니 항목 생성 테스트 - 해당 상품이 장바구니 없을 경우")
	void createCartItemForGuest_createNew() {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(null, "session123", 1L, 2);
		ProductImage productImage = new ProductImage(product, "image.jpg");
		product.getProductImage().add(productImage);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(redisTemplate.opsForValue().get(hashName + request.getSessionId())).thenReturn(null);
		when(objectMapper.convertValue(null, CartDTO.class)).thenReturn(null);

		// when
		int size = cartService.createCartItemForGuest(request);

		// then
		assertEquals(1, size);
		verify(valueOperations).set(eq(hashName + request.getSessionId()), any(CartDTO.class), eq(Duration.ofHours(2)));
	}

	@Test
	@DisplayName("장바구니 항목 생성 테스트 - 이미 상품 존재하여 수량만 적용")
	void createCartItemForGuest_mergeQuantity() {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(null, "session123", 1L, 3);

		CartItemDTO existingItem = new CartItemDTO(1L, "상품1", 10000L, 9000L, BigDecimal.ZERO, "image.jpg", 1);
		CartDTO cart = new CartDTO();
		cart.setCartItems(new ArrayList<>(List.of(existingItem)));

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(redisTemplate.opsForValue().get(hashName + request.getSessionId())).thenReturn(cart);
		when(objectMapper.convertValue(cart, CartDTO.class)).thenReturn(cart);

		// when
		int size = cartService.createCartItemForGuest(request);

		// then
		assertEquals(1, size);
		assertEquals(4, existingItem.getCartItemsQuantity()); // 기존 1 + 3
		verify(valueOperations).set(eq(hashName + request.getSessionId()), any(CartDTO.class), eq(Duration.ofHours(2)));
	}

	@Test
	@DisplayName("장바구니 항목 생성 테스트 - 실패(상품이 없을 경우)")
	void createCartItemForGuest_NotFoundProduct() {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(null, "session123", 999L, 1);
		when(productRepository.findById(999L)).thenReturn(Optional.empty());

		// when & then
		assertThrows(ProductNotFoundException.class, () -> cartService.createCartItemForGuest(request));
	}

	@Test
	@DisplayName("장바구니 항목 생성 테스트 - 실패(판매중이 아닌 상품일 경우)")
	void createCartItemForGuest_productNotForSale() {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(null, "session123", 1L, 1);
		Product mockProduct = mock(Product.class);
		when(mockProduct.getProductState()).thenReturn(new ProductState(2L, ProductStateName.OUT));

		when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

		// when & then
		assertThrows(ProductNotForSaleException.class, () -> cartService.createCartItemForGuest(request));
	}

	@Test
	@DisplayName("게스트 장바구니 항목 수량 변경 테스트")
	void updateCartItemForGuest() {
		// given
		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO("", "session123", 1L, 5);

		CartItemDTO cartItem = new CartItemDTO(1L, "Product 1", 1000, 500, new BigDecimal(50), "/image1.jpg", 2);
		CartDTO cart = new CartDTO(List.of(cartItem));

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(redisTemplate.opsForValue().get(hashName + request.getSessionId())).thenReturn(cart);
		when(objectMapper.convertValue(cart, CartDTO.class)).thenReturn(cart);

		// when
		cartService.updateCartItemForGuest(request);

		// then
		assertThat(cartItem.getCartItemsQuantity()).isEqualTo(5);
		verify(valueOperations).set(eq(hashName + request.getSessionId()), any(CartDTO.class), eq(Duration.ofHours(2)));
	}

	@Test
	@DisplayName("게스트 장바구니 항목 삭제 테스트")
	void deleteCartItemForGuest_shouldRemoveItem() {
		// given
		CartItemDTO item = new CartItemDTO(1L, "Product 1", 1000, 500, new BigDecimal(50), "/image1.jpg", 2);
		CartDTO cart = new CartDTO(new ArrayList<>(List.of(item)));
		RequestDeleteCartItemsForGuestDTO request = new RequestDeleteCartItemsForGuestDTO("session123", 1L);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(redisTemplate.opsForValue().get(hashName + request.getSessionId())).thenReturn(cart);
		when(objectMapper.convertValue(cart, CartDTO.class)).thenReturn(cart);

		// when
		cartService.deleteCartItemForGuest(request);

		// then
		assertEquals(0, cart.getCartItems().size());
		verify(valueOperations).set(eq(hashName + request.getSessionId()), any(CartDTO.class), eq(Duration.ofHours(2)));
	}

	@Test
	@DisplayName("게스트 장바구니 항목 전체 삭제 테스트")
	void deleteCartForGuest() {
		// when
		cartService.deleteCartForGuest("session123");

		// then
		verify(redisTemplate).delete(hashName + "session123");
	}

	@Test
	@DisplayName("게스트 장바구니 목록 조회 테스트")
	void getCartItemsByGuest() {
		// given
		CartItemDTO item1 = new CartItemDTO(1L, "Product 1", 1000, 500, new BigDecimal(50), "/image1.jpg", 2);
		CartItemDTO item2 = new CartItemDTO(2L, "Product 2", 1000, 500, new BigDecimal(50), "/image2.jpg", 3);
		CartDTO cart = new CartDTO(List.of(item1, item2));

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(redisTemplate.opsForValue().get(hashName + "session123")).thenReturn(cart);
		when(objectMapper.convertValue(cart, CartDTO.class)).thenReturn(cart);

		when(minioUtils.getPresignedUrl("e2bs-products-image", "/image1.jpg")).thenReturn("https://example.com/image1.jpg");
		when(minioUtils.getPresignedUrl("e2bs-products-image", "/image2.jpg")).thenReturn("https://example.com/image2.jpg");

		// when
		List<ResponseCartItemsForGuestDTO> result = cartService.getCartItemsByGuest("session123");

		// then
		assertEquals(2, result.size());
	}

}
