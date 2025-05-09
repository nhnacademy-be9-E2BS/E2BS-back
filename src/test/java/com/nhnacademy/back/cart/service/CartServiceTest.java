package com.nhnacademy.back.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.cart.domain.dto.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestDeleteCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsDTO;
import com.nhnacademy.back.cart.domain.entity.Cart;
import com.nhnacademy.back.cart.domain.entity.CartItems;
import com.nhnacademy.back.cart.exception.CartItemAlreadyExistsException;
import com.nhnacademy.back.cart.repository.CartItemsJpaRepository;
import com.nhnacademy.back.cart.repository.CartJpaRepository;
import com.nhnacademy.back.cart.service.impl.CartServiceImpl;
import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

	@Mock
	private CustomerJpaRepository customerRepository;

	@Mock
	private ProductJpaRepository productRepository;

	@Mock
	private CartJpaRepository cartRepository;

	@Mock
	private CartItemsJpaRepository cartItemsRepository;

	@InjectMocks
	private CartServiceImpl cartService;


	private final long customerId = 1L;
	private final long productId = 1L;
	private final long cartItemId = 1L;

	private Customer customer;
	private Product product;
	private Cart cart;

	@BeforeEach
	void setup() {
		customer = new Customer("abc@gmail.com", "pwd12345", "name1");
		cart = new Cart(customer);

		product = new Product(productId, new ProductState(ProductStateName.SALE), new Publisher("a"),
			        "title1", "content1", "description", LocalDate.now(), "isbn",
			   10000, 8000, false, 1, 0, 0, null);
	}


	@Test
	@DisplayName("비회원/회원 장바구니 항목 추가 테스트")
	void createCartItem() {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(customerId, "", productId, 2);

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(cartRepository.existsByCustomer_CustomerId(customerId)).thenReturn(false);
		when(cartRepository.save(any(Cart.class))).thenReturn(cart);
		when(cartItemsRepository.existsByCartAndProduct(cart, product)).thenReturn(false);

		// when
		cartService.createCartItem(request);

		// then
		verify(cartItemsRepository).save(any(CartItems.class));
	}

	@Test
	@DisplayName("비회원/회원 장바구니 항목 추가 테스트 - 실패(아이템 항목 이미 존재한 경우)")
	void createCartItem_AlreadyExists() {
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(customerId, "", productId, 2);

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(cartRepository.existsByCustomer_CustomerId(customerId)).thenReturn(true);
		when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(cart);
		when(cartItemsRepository.existsByCartAndProduct(cart, product)).thenReturn(true);

		assertThrows(CartItemAlreadyExistsException.class, () -> cartService.createCartItem(request));
	}

	@Test
	@DisplayName("비회원/회원 장바구니 수량 변경 테스트")
	void updateCartItem() {
		// given
		CartItems cartItem = new CartItems(cart, product, 1);
		when(cartItemsRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO("", 5);

		// when
		cartService.updateCartItem(cartItemId, request);

		// then
		assertEquals(5, cartItem.getCartItemsQuantity());
	}

	@Test
	@DisplayName("비회원/회원 장바구니 항목 삭제 테스트")
	void deleteCartItem() {
		// given
		CartItems cartItem = new CartItems(cart, product, 1);
		when(cartItemsRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

		// when
		cartService.deleteCartItem(cartItemId, new RequestDeleteCartItemsDTO());

		// then
		verify(cartItemsRepository).delete(cartItem);
	}

	@Test
	@DisplayName("비회원/회원의 장바구니 목록 조회 테스트")
	void getCartItemsByCustomer() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		CartItems cartItem = new CartItems(cart, product, 2);
		Page<CartItems> page = new PageImpl<>(List.of(cartItem));

		when(cartItemsRepository.findByCart_Customer_CustomerId(customerId, pageable)).thenReturn(page);

		// when
		Page<ResponseCartItemsDTO> result = cartService.getCartItemsByCustomer(customerId, pageable);

		// then
		assertEquals(1, result.getTotalElements());
		assertEquals(productId, result.getContent().getFirst().getProductId());
	}
}
