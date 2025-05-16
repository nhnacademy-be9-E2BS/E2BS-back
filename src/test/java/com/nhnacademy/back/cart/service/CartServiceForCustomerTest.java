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

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForCustomerDTO;
import com.nhnacademy.back.cart.domain.entity.Cart;
import com.nhnacademy.back.cart.domain.entity.CartItems;
import com.nhnacademy.back.cart.exception.CartItemAlreadyExistsException;
import com.nhnacademy.back.cart.exception.CartNotFoundException;
import com.nhnacademy.back.cart.repository.CartItemsJpaRepository;
import com.nhnacademy.back.cart.repository.CartJpaRepository;
import com.nhnacademy.back.cart.service.impl.CartServiceImpl;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@ExtendWith(MockitoExtension.class)
class CartServiceForCustomerTest {

	@Mock
	private CustomerJpaRepository customerRepository;

	@Mock
	private ProductJpaRepository productRepository;

	@Mock
	private ProductCategoryJpaRepository productCategoryRepository;

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
	void createCartItemForCustomer() {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(customerId, "", productId, 2);

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(cartRepository.existsByCustomer_CustomerId(customerId)).thenReturn(false);
		when(cartRepository.save(any(Cart.class))).thenReturn(cart);
		when(cartItemsRepository.existsByCartAndProduct(cart, product)).thenReturn(false);

		// when
		cartService.createCartItemForCustomer(request);

		// then
		verify(cartItemsRepository).save(any(CartItems.class));
	}

	@Test
	@DisplayName("비회원/회원 장바구니 항목 추가 테스트 - 실패(아이템 항목 이미 존재한 경우)")
	void createCartItemForCustomer_AlreadyExists() {
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(customerId, "", productId, 2);

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(cartRepository.existsByCustomer_CustomerId(customerId)).thenReturn(true);
		when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(cart);
		when(cartItemsRepository.existsByCartAndProduct(cart, product)).thenReturn(true);

		assertThrows(CartItemAlreadyExistsException.class, () -> cartService.createCartItemForCustomer(request));
	}

	@Test
	@DisplayName("비회원/회원 장바구니 항목 수량 변경 테스트")
	void updateCartItemForCustomer() {
		// given
		CartItems cartItem = new CartItems(cart, product, 1);
		when(cartItemsRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO(null, null, 5);

		// when
		cartService.updateCartItemForCustomer(cartItemId, request);

		// then
		assertEquals(5, cartItem.getCartItemsQuantity());
	}

	@Test
	@DisplayName("비회원/회원 장바구니 항목 삭제 테스트")
	void deleteCartItemForCustomer() {
		// given
		CartItems cartItem = new CartItems(cart, product, 1);
		when(cartItemsRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

		// when
		cartService.deleteCartItemForCustomer(cartItemId);

		// then
		verify(cartItemsRepository).delete(cartItem);
	}

	@Test
	@DisplayName("비회원/회원 장바구니 항목 전체 삭제 테스트")
	void deleteCartForCustomer() {
		// given
		long customerId = 1L;
		Cart cart = new Cart();
		when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(cart);

		// when
		cartService.deleteCartForCustomer(customerId);

		// then
		verify(cartRepository, times(1)).delete(cart);
	}

	@Test
	@DisplayName("비회원/회원 장바구니 항목 전체 삭제 테스트 - 실패(찾을 수 없을 때의 예외)")
	void deleteCartForCustomer_CartNotFound() {
		// given
		long customerId = 1L;
		when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(null);

		// when & then
		assertThrows(CartNotFoundException.class, () ->
			cartService.deleteCartForCustomer(customerId)
		);
	}
	

	@Test
	@DisplayName("비회원/회원의 장바구니 목록 조회 테스트")
	void getCartItemsByCustomer() {
		// given
		CartItems cartItem = new CartItems(cart, product, 2);
		List<CartItems> cartItems = List.of(cartItem);

		when(cartItemsRepository.findByCart_Customer_CustomerId(customerId)).thenReturn(cartItems);
		when(productCategoryRepository.findByProduct_ProductId(customerId)).thenReturn(List.of());

		// when
		List<ResponseCartItemsForCustomerDTO> result = cartService.getCartItemsByCustomer(customerId);

		// then
		assertEquals(1, result.size());
		assertEquals(productId, result.getFirst().getProductId());
	}
}
