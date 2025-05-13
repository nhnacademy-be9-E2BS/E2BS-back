package com.nhnacademy.back.cart.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.cart.domain.dto.CartDTO;
import com.nhnacademy.back.cart.domain.dto.CartItemDTO;
import com.nhnacademy.back.cart.domain.dto.ProductCategoryDTO;
import com.nhnacademy.back.cart.domain.dto.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsForCustomerDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.entity.Cart;
import com.nhnacademy.back.cart.domain.entity.CartItems;
import com.nhnacademy.back.cart.exception.CartItemAlreadyExistsException;
import com.nhnacademy.back.cart.exception.CartItemNotFoundException;
import com.nhnacademy.back.cart.exception.CartNotFoundException;
import com.nhnacademy.back.cart.repository.CartItemsJpaRepository;
import com.nhnacademy.back.cart.repository.CartJpaRepository;
import com.nhnacademy.back.cart.service.CartService;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CustomerJpaRepository customerRepository;
	private final ProductJpaRepository productRepository;
	private final ProductCategoryJpaRepository productCategoryRepository;
	private final CartJpaRepository cartRepository;
	private final CartItemsJpaRepository cartItemsRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;


	/**
	 * 비회원/회원일 때 장바구니 항목 생성 메소드
	 */
	@Override
	public void createCartItemForCustomer(RequestAddCartItemsDTO request) {
		// 비회원/회원, 상품 존재 검증
		Customer findCustomer = customerRepository.findById(request.getCustomerId())
			.orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
		Product findProduct = productRepository.findById(request.getProductId())
			.orElseThrow(() -> new ProductNotFoundException("Product not found"));

		Cart cart;
		// 장바구니가 없으면 장바구니 생성
		if (!cartRepository.existsByCustomer_CustomerId(request.getCustomerId())) {
			cart = cartRepository.save(new Cart(findCustomer));
		} else {
			cart = cartRepository.findByCustomer_CustomerId(request.getCustomerId());
		}

		// 현재 고객이 장바구니 아이템을 가지고 있을 수 있으므로 중복 검증
		if (cartItemsRepository.existsByCartAndProduct(cart, findProduct)) {
			throw new CartItemAlreadyExistsException("Cart item already exists");
		}

		// 장바구니 아이템 생성
		cartItemsRepository.save(new CartItems(cart, findProduct, request.getQuantity()));
	}

	/**
	 * 비회원/회원일 때 장바구니 항목 수량 수정 메소드
	 */
	@Override
	public void updateCartItemForCustomer(long cartItemId, RequestUpdateCartItemsDTO request) {
		// 장바구니 항목 존재 검증
		CartItems findCartItem = cartItemsRepository.findById(cartItemId)
			.orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

		// 해당 장바구니 아이템의 수량 변경
		findCartItem.changeCartItemsQuantity(request.getQuantity());
	}

	/**
	 * 비회원/회원일 때 장바구니 항목 삭제 메소드
	 */
	@Override
	public void deleteCartItemForCustomer(long cartItemId) {
		// 장바구니 항목 존재 검증
		CartItems findCartItem = cartItemsRepository.findById(cartItemId)
			.orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

		// 해당 장바구니 아이템 삭제
		cartItemsRepository.delete(findCartItem);
	}

	/**
	 * 비회원/회원일 때 장바구니 전체 삭제 메소드
	 */
	@Override
	public void deleteCartForCustomer(long customerId) {
		Cart findCart = cartRepository.findByCustomer_CustomerId(customerId);
		if (Objects.isNull(findCart)) {
			throw new CartNotFoundException("Cart not found");
		}

		cartRepository.delete(findCart);
	}

	/**
	 * 비회원/회원인 고객의 장바구니 목록 조회 메소드
	 */
	@Override
	public List<ResponseCartItemsForCustomerDTO> getCartItemsByCustomer(long customerId) {
		// 고객이 담은 장바구니들을 리스트로 담음
		List<CartItems> cartItems = cartItemsRepository.findByCart_Customer_CustomerId(customerId);

		// 필요한 api 스펙에 맞춰 response 재가공해서 반환
		return cartItems.stream()
			.map(cartItem -> {
				// 기본 이미지 설정
				Product product = cartItem.getProduct();
				String productImagePath = "default.jpg";

				if (Objects.nonNull(product.getProductImage())) {
					productImagePath = product.getProductImage().getFirst().getProductImagePath();
				}

				List<ProductCategory> findProductCategories = productCategoryRepository.findByProduct_ProductId(product.getProductId());
				List<ProductCategoryDTO> findProductCategoriesDto = findProductCategories.stream()
					.map(productCategory -> new ProductCategoryDTO(productCategory.getCategory().getCategoryId()))
					.toList();

				// ResponseCartItemsForCustomerDTO 객체 반환
				return new ResponseCartItemsForCustomerDTO(
					cartItem.getCartItemsId(),
					product.getProductId(),
					findProductCategoriesDto,
					product.getProductTitle(),
					product.getProductSalePrice(),
					productImagePath,
					cartItem.getCartItemsQuantity());
			})
			.toList();
	}


	/**
	 * 게스트일 때 장바구니 항목 생성 메소드
	 */
	@Override
	public void createCartItemForGuest(RequestAddCartItemsDTO request) {
		// 상품 존재 검증
		Product findProduct = productRepository.findById(request.getProductId())
			.orElseThrow(() -> new ProductNotFoundException("Product not found"));

		List<ProductCategory> findProductCategories = productCategoryRepository.findByProduct_ProductId(findProduct.getProductId());
		List<ProductCategoryDTO> findProductCategoriesDto = findProductCategories.stream()
			.map(productCategory -> new ProductCategoryDTO(productCategory.getCategory().getCategoryId()))
			.toList();

		// Redis에서 장바구니 가져오기 (없으면 생성)
		Object o = redisTemplate.opsForValue().get(request.getSessionId());
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			cart = new CartDTO();
		}

		boolean alreadyExists = cart.getCartItems().stream()
			.anyMatch(item -> item.getProductId() == (findProduct.getProductId()));
		if (alreadyExists) {
			throw new CartItemAlreadyExistsException("Cart item already exists");
		}

		// 장바구니 항목 생성 및 Cart에 추가
		String image = "";
		if(Objects.nonNull(findProduct.getProductImage())) {
			image = findProduct.getProductImage().getFirst().getProductImagePath();
		}

		CartItemDTO newItem = new CartItemDTO(
			findProduct.getProductId(),
			findProductCategoriesDto,
			findProduct.getProductTitle(),
			findProduct.getProductSalePrice(),
			image,
			request.getQuantity()
		);
		cart.getCartItems().add(newItem);

		// 변경된 Cart 객체를 Redis에 저장
		redisTemplate.opsForValue().set(request.getSessionId(), cart);
	}

	/**
	 * 게스트일 때 장바구니 항목 수량 변경 메소드
	 */
	@Override
	public void updateCartItemForGuest(RequestUpdateCartItemsDTO request) {
		Object o = redisTemplate.opsForValue().get(request.getSessionId());
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			throw new CartNotFoundException("Cart not found in session");
		}

		// 장바구니 항목 찾기 및 수량 변경
		CartItemDTO cartItem = cart.getCartItems().stream()
			.filter(item -> Objects.equals(item.getProductId(), request.getProductId()))
			.findFirst()
			.orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

		cartItem.setCartItemsQuantity(request.getQuantity());

		// 해당 항목이 0개일 때 삭제
		if (cartItem.getCartItemsQuantity() == 0) {
			cart.getCartItems().removeIf(item -> item.getProductId() == request.getProductId());
		}

		// 변경된 Cart 객체를 Redis에 저장
		redisTemplate.opsForValue().set(request.getSessionId(), cart);
	}

	/**
	 * 게스트일 때 장바구니 항목 삭제 메소드
	 */
	@Override
	public void deleteCartItemForGuest(RequestDeleteCartItemsForGuestDTO request) {
		Object o = redisTemplate.opsForValue().get(request.getSessionId());
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			throw new CartItemNotFoundException("Cart not found in session");
		}

		// 해당 항목 삭제
		cart.getCartItems().removeIf(item -> item.getProductId() == request.getProductId());

		// 삭제된 Cart 객체를 Redis에 저장
		redisTemplate.opsForValue().set(request.getSessionId(), cart);
	}

	/**
	 * 게스트일 때 장바구니 항목 전체 삭제 메소드
	 */
	@Override
	public void deleteCartForGuest(String sessionId) {
		Object o = redisTemplate.opsForValue().get(sessionId);
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			throw new CartItemNotFoundException("Cart not found in session");
		}

		redisTemplate.delete(sessionId);
	}

	/**
	 * 게스트인 고객의 장바구니 목록 조회
	 */
	@Override
	public List<ResponseCartItemsForGuestDTO> getCartItemsByGuest(String sessionId) {
		Object o = redisTemplate.opsForValue().get(sessionId);
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart) || cart.getCartItems().isEmpty()) {
			return List.of();
		}

		List<CartItemDTO> cartItems = cart.getCartItems();

		// DTO 가공
		return cartItems.stream()
			.map(cartItem -> new ResponseCartItemsForGuestDTO(
				cartItem.getProductId(),
				cartItem.getCategoryIds(),
				cartItem.getProductTitle(),
				cartItem.getProductSalePrice(),
				cartItem.getProductImagePath(),
				cartItem.getCartItemsQuantity()
			))
			.collect(Collectors.toList());
	}

}
