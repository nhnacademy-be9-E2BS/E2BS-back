package com.nhnacademy.back.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.entity.Cart;
import com.nhnacademy.back.cart.domain.entity.CartItems;
import com.nhnacademy.back.cart.exception.CartNotFoundException;
import com.nhnacademy.back.cart.repository.CartItemsJpaRepository;
import com.nhnacademy.back.cart.repository.CartJpaRepository;
import com.nhnacademy.back.cart.service.impl.CartServiceImpl;
import com.nhnacademy.back.order.deliveryfee.repository.DeliveryFeeJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@ExtendWith(MockitoExtension.class)
class CartServiceForMemberTest {

	@Mock
	private CustomerJpaRepository customerRepository;

	@Mock
	private MemberJpaRepository memberRepository;

	@Mock
	private ProductJpaRepository productRepository;

	@Mock
	private DeliveryFeeJpaRepository deliveryFeeRepository;

	@Mock
	private CartJpaRepository cartRepository;

	@Mock
	private CartItemsJpaRepository cartItemsRepository;

	@InjectMocks
	private CartServiceImpl cartService;

	private final long customerId = 1L;
	private final String memberId = "id123";
	private final long productId = 1L;
	private final long cartItemId = 1L;

	private Customer customer;
	private Member member;
	private Product product;
	private Cart cart;

	@BeforeEach
	void setup() {
		customer = new Customer(customerId, "abc@gmail.com", "pwd12345", "name1");
		member = mock(Member.class);

		cart = new Cart(customer);

		product = new Product(productId, new ProductState(ProductStateName.SALE), new Publisher("a"),
			"title1", "content1", "description", LocalDate.now(), "isbn",
			10000, 8000, false, 1, null);
	}

	@Test
	@DisplayName("회원 장바구니 항목 추가 테스트")
	void createCartItemForCustomer() {
		// given
		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(memberId, "", productId, 2);

		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(cartRepository.existsByCustomer_CustomerId(customerId)).thenReturn(false);
		when(cartRepository.save(any(Cart.class))).thenReturn(cart);
		when(cartItemsRepository.existsByCartAndProduct(cart, product)).thenReturn(false);

		// when
		cartService.createCartItemForMember(request);

		// then
		verify(cartItemsRepository).save(any(CartItems.class));
	}

	@Test
	@DisplayName("회원 장바구니 항목 수량 변경 테스트")
	void updateCartItemForCustomer() {
		// given
		CartItems cartItem = new CartItems(cart, product, 1);

		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);
		when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(Optional.of(cart));
		when(cartItemsRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO("id123", null, 1L, 5);

		// when
		cartService.updateCartItemForMember(cartItemId, request);

		// then
		assertEquals(5, cartItem.getCartItemsQuantity());
	}

	@Test
	@DisplayName("회원 장바구니 항목 삭제 테스트")
	void deleteCartItemForCustomer() {
		// given
		CartItems cartItem = new CartItems(cart, product, 1);
		when(cartItemsRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

		// when
		cartService.deleteCartItemForMember(cartItemId);

		// then
		verify(cartItemsRepository).delete(cartItem);
	}

	@Test
	@DisplayName("회원 장바구니 항목 전체 삭제 테스트")
	void deleteCartForCustomer() {
		// given
		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);
		when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(Optional.of(cart));

		// when
		cartService.deleteCartForMember(memberId);

		// then
		verify(cartRepository, times(1)).delete(cart);
	}

	@Test
	@DisplayName("회원 장바구니 항목 전체 삭제 테스트 - 실패(찾을 수 없을 때의 예외)")
	void deleteCartForCustomer_CartNotFound() {
		// given
		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);
		when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(CartNotFoundException.class, () ->
			cartService.deleteCartForMember(memberId)
		);
	}

	// @Test
	// @DisplayName("회원의 장바구니 목록 조회 테스트")
	// void getCartItemsByCustomer() {
	// 	// given
	// 	CartItems cartItem = new CartItems(cart, product, 2);
	// 	List<CartItems> cartItems = List.of(cartItem);
	// 	DeliveryFee deliveryFee = new DeliveryFee(new RequestDeliveryFeeDTO(1000, 5000));
	//
	// 	when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
	// 	when(member.getCustomerId()).thenReturn(customerId);
	// 	when(cartItemsRepository.findByCart_Customer_CustomerId(customerId)).thenReturn(cartItems);
	// 	when(deliveryFeeRepository.findTopByOrderByDeliveryFeeDateDesc()).thenReturn(deliveryFee);
	//
	// 	// when
	// 	List<ResponseCartItemsForMemberDTO> result = cartService.getCartItemsByMember(memberId);
	//
	// 	// then
	// 	assertEquals(1, result.size());
	// 	assertEquals(productId, result.getFirst().getProductId());
	// }
}
