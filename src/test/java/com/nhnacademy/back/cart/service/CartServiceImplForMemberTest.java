// package com.nhnacademy.back.cart.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.nhnacademy.back.account.customer.domain.entity.Customer;
// import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
// import com.nhnacademy.back.account.member.domain.entity.Member;
// import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
// import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
// import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
// import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
// import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForMemberDTO;
// import com.nhnacademy.back.cart.domain.entity.Cart;
// import com.nhnacademy.back.cart.domain.entity.CartItems;
// import com.nhnacademy.back.cart.exception.CartItemNotFoundException;
// import com.nhnacademy.back.cart.exception.CartNotFoundException;
// import com.nhnacademy.back.cart.repository.CartItemsJpaRepository;
// import com.nhnacademy.back.cart.repository.CartJpaRepository;
// import com.nhnacademy.back.cart.service.impl.CartServiceImpl;
// import com.nhnacademy.back.common.util.MinioUtils;
// import com.nhnacademy.back.product.image.domain.entity.ProductImage;
// import com.nhnacademy.back.product.product.domain.entity.Product;
// import com.nhnacademy.back.product.product.exception.ProductNotForSaleException;
// import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
// import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
// import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
// import com.nhnacademy.back.product.state.domain.entity.ProductState;
// import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
//
// @ExtendWith(MockitoExtension.class)
// class CartServiceImplForMemberTest {
//
// 	@Mock
// 	private CustomerJpaRepository customerRepository;
//
// 	@Mock
// 	private MemberJpaRepository memberRepository;
//
// 	@Mock
// 	private ProductJpaRepository productRepository;
//
// 	@Mock
// 	private CartJpaRepository cartRepository;
//
// 	@Mock
// 	private CartItemsJpaRepository cartItemsRepository;
//
// 	@Mock
// 	private MinioUtils minioUtils;
//
// 	@InjectMocks
// 	private CartServiceImpl cartService;
//
//
// 	private final long customerId = 1L;
// 	private final String memberId = "id123";
// 	private final long productId = 1L;
// 	private final long cartItemId = 1L;
//
// 	private Customer customer;
// 	private Member member;
// 	private Product product;
// 	private Cart cart;
//
// 	@BeforeEach
// 	void setup() {
// 		customer = new Customer(customerId, "abc@gmail.com", "pwd12345", "name1");
// 		member = mock(Member.class);
//
// 		cart = new Cart(customer);
//
// 		product = new Product(productId, new ProductState(1L, ProductStateName.SALE), new Publisher("a"),
// 			"title1", "content1", "description", LocalDate.now(), "isbn",
// 			10000, 8000, false, 1, null);
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 생성 테스트")
// 	void createCartForMember() {
// 		// given
// 		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
// 		when(customerRepository.findById(member.getCustomerId())).thenReturn(Optional.of(customer));
//
// 		// when
// 		cartService.createCartForMember(memberId);
//
// 		// then
// 		verify(cartRepository).save(any(Cart.class));
// 	}
// 	@Test
// 	@DisplayName("회원 장바구니 항목 추가 테스트")
// 	void createCartItemForMember() {
// 		// given
// 		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(memberId, "", productId, 2);
//
// 		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
// 		when(member.getCustomerId()).thenReturn(customerId);
// 		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
// 		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
// 		when(cartRepository.existsByCustomer_CustomerId(customerId)).thenReturn(false);
// 		when(cartRepository.save(any(Cart.class))).thenReturn(cart);
// 		when(cartItemsRepository.existsByCartAndProduct(cart, product)).thenReturn(false);
//
// 		// when
// 		cartService.createCartItemForMember(request);
//
// 		// then
// 		verify(cartItemsRepository).save(any(CartItems.class));
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 항목 추가 테스트 - 실패(회원을 찾지 못한 경우)")
// 	void createCartItemForMember_Fail_NotExistMember() {
// 		// given
// 		RequestAddCartItemsDTO requestDto = new RequestAddCartItemsDTO(memberId, null, 1L, 1);
// 		when(memberRepository.getMemberByMemberId(any())).thenReturn(null);
//
// 		// when & then
// 		assertThrows(NotFoundMemberException.class, () -> cartService.createCartItemForMember(requestDto));
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 항목 추가 테스트- 실패(판매 중 아닌 상품)")
// 	void createCartItemForMember_Fail_ProductNotForSale() {
// 		// given
// 		Product newProduct = new Product(productId, new ProductState(2L, ProductStateName.OUT), new Publisher("a"),
// 			"title1", "content1", "description", LocalDate.now(), "isbn",
// 			10000, 8000, false, 1, null);
//
// 		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(memberId, "", productId, 2);
// 		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
// 		when(member.getCustomerId()).thenReturn(customerId);
// 		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
// 		when(productRepository.findById(productId)).thenReturn(Optional.of(newProduct));
//
// 		// when & then
// 		assertThrows(ProductNotForSaleException.class, () -> cartService.createCartItemForMember(request));
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 항목 추가 테스트 - 기존 상품 병합")
// 	void createCartItemForMember_MergeWithExistingItem() {
// 		// given
// 		RequestAddCartItemsDTO request = new RequestAddCartItemsDTO(memberId, "", productId, 2);
// 		CartItems existingCartItem = mock(CartItems.class);
//
// 		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
// 		when(member.getCustomerId()).thenReturn(customerId);
// 		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
// 		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
// 		when(cartRepository.existsByCustomer_CustomerId(customerId)).thenReturn(true);
// 		when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(Optional.of(cart));
// 		when(cartItemsRepository.existsByCartAndProduct(cart, product)).thenReturn(true);
// 		when(cartItemsRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingCartItem));
//
// 		// when
// 		cartService.createCartItemForMember(request);
//
// 		// then
// 		verify(existingCartItem).changeCartItemsQuantity(anyInt());
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 항목 수량 변경 테스트")
// 	void updateCartItemForCustomer() {
// 		// given
// 		CartItems cartItem = new CartItems(cart, product, 1);
//
// 		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
// 		when(member.getCustomerId()).thenReturn(customerId);
// 		when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(Optional.of(cart));
// 		when(cartItemsRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
//
// 		RequestUpdateCartItemsDTO request = new RequestUpdateCartItemsDTO("id123", null, 1L, 5);
//
// 		// when
// 		cartService.updateCartItemForMember(cartItemId, request);
//
// 		// then
// 		assertEquals(5, cartItem.getCartItemsQuantity());
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 항목 삭제 테스트")
// 	void deleteCartItemForCustomer() {
// 		// given
// 		CartItems cartItem = new CartItems(cart, product, 1);
// 		when(cartItemsRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
//
// 		// when
// 		cartService.deleteCartItemForMember(cartItemId);
//
// 		// then
// 		verify(cartItemsRepository).delete(cartItem);
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 항목 삭제 테스트 - 실패(장바구니 항목을 찾지 못한 경우)")
// 	void deleteCartItemForMember_Fail_NotFoundCartItem() {
// 		// given
// 		when(cartItemsRepository.findById(anyLong())).thenReturn(Optional.empty());
//
// 		// when & then
// 		assertThrows(CartItemNotFoundException.class, ()-> cartService.deleteCartItemForMember(99L));
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 항목 전체 삭제 테스트")
// 	void deleteCartForCustomer() {
// 		// given
// 		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
// 		when(member.getCustomerId()).thenReturn(customerId);
// 		when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(Optional.of(cart));
//
// 		// when
// 		cartService.deleteCartForMember(memberId);
//
// 		// then
// 		verify(cartRepository, times(1)).delete(cart);
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 항목 전체 삭제 테스트 - 실패(찾을 수 없을 때의 예외)")
// 	void deleteCartForCustomer_NotFoundCart() {
// 		// given
// 		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(member);
// 		when(member.getCustomerId()).thenReturn(customerId);
// 		when(cartRepository.findByCustomer_CustomerId(customerId)).thenReturn(Optional.empty());
//
// 		// when & then
// 		assertThrows(CartNotFoundException.class, () ->
// 			cartService.deleteCartForMember(memberId)
// 		);
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 목록 조회 테스트 - 정상 케이스")
// 	void getCartItemsByMember() {
// 		// given
// 		Product newProduct = Product.builder()
// 			.productId(productId)
// 			.productTitle("상품A")
// 			.productRegularPrice(10000L)
// 			.productSalePrice(8000L)
// 			.productImage(new ArrayList<>())
// 			.build();
//
// 		ProductImage image = new ProductImage(newProduct, "product.jpg");
// 		newProduct.getProductImage().add(image);
//
// 		CartItems cartItem = mock(CartItems.class);
// 		when(cartItem.getCartItemsId()).thenReturn(1L);
// 		when(cartItem.getProduct()).thenReturn(newProduct);
// 		when(cartItem.getCartItemsQuantity()).thenReturn(2);
//
// 		Member mockMember = mock(Member.class);
// 		when(mockMember.getCustomerId()).thenReturn(100L);
//
// 		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);
// 		when(cartItemsRepository.findByCart_Customer_CustomerId(100L)).thenReturn(List.of(cartItem));
//
// 		when(minioUtils.getPresignedUrl("e2bs-products-image", "product.jpg")).thenReturn("https://example.com/image1.jpg");
//
// 		// when
// 		List<ResponseCartItemsForMemberDTO> result = cartService.getCartItemsByMember(memberId);
//
// 		// then
// 		assertEquals(1, result.size());
// 		ResponseCartItemsForMemberDTO dto = result.getFirst();
// 		assertEquals("상품A", dto.getProductTitle());
// 		assertEquals("https://example.com/image1.jpg", dto.getProductImagePath());
// 		assertEquals(BigDecimal.valueOf(20), dto.getDiscountRate());
// 		assertEquals(16000L, dto.getProductTotalPrice());
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 목록 조회 테스트 - 실패(상품이 없는 경우)")
// 	void getCartItemsByMember_Fail_NotFoundProduct() {
// 		// given
// 		CartItems cartItem = mock(CartItems.class);
// 		when(cartItem.getProduct()).thenReturn(null);
//
// 		Member mockMember = mock(Member.class);
// 		when(mockMember.getCustomerId()).thenReturn(300L);
//
// 		when(memberRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);
// 		when(cartItemsRepository.findByCart_Customer_CustomerId(300L)).thenReturn(List.of(cartItem));
//
// 		// when & then
// 		assertThrows(ProductNotFoundException.class, () -> cartService.getCartItemsByMember(memberId));
// 	}
//
//
// }
