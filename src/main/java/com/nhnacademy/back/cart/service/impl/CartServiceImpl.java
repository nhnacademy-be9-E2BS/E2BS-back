package com.nhnacademy.back.cart.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.cart.domain.dto.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestDeleteCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.ResponseCartItemsDTO;
import com.nhnacademy.back.cart.domain.entity.Cart;
import com.nhnacademy.back.cart.domain.entity.CartItems;
import com.nhnacademy.back.cart.exception.CartItemAlreadyExistsException;
import com.nhnacademy.back.cart.exception.CartItemNotFoundException;
import com.nhnacademy.back.cart.exception.CartNotFoundException;
import com.nhnacademy.back.cart.repository.CartItemsJpaRepository;
import com.nhnacademy.back.cart.repository.CartJpaRepository;
import com.nhnacademy.back.cart.service.CartService;
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
	private final CartJpaRepository cartRepository;
	private final CartItemsJpaRepository cartItemsRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * 장바구니 아이템 생성 메소드
	 */
	@Override
	public void createCartItem(RequestAddCartItemsDTO request) {
		if (Objects.isNull(request.getCustomerId())) {
			createCartItemForGuest(request);
		} else {
			createCartItemForCustomer(request);
		}
	}
	/// 게스트일 때 장바구니 항목 생성 메소드
	private void createCartItemForGuest(RequestAddCartItemsDTO request) {
		// 상품 존재 검증
		Product findProduct = productRepository.findById(request.getProductId())
			.orElseThrow(() -> new ProductNotFoundException("Product not found"));

		// Redis에서 장바구니 가져오기 (없으면 생성)
		Cart cart = (Cart) redisTemplate.opsForValue().get(request.getSessionId());
		if (Objects.isNull(cart)) {
			cart = new Cart();
		}

		// 현재 고객이 장바구니 아이템을 가지고 있을 수 있으므로 중복 검증
		if (cart.getCartItems().stream().anyMatch(cartItem -> cartItem.getProduct().getProductId() == findProduct.getProductId())) {
			throw new CartItemAlreadyExistsException("Cart item already exists in session");
		}

		// 장바구니 항목 생성 및 Cart에 추가
		CartItems cartItem = new CartItems(cart, findProduct, request.getQuantity());
		cart.getCartItems().add(cartItem);

		// 변경된 Cart 객체를 Redis에 저장
		redisTemplate.opsForValue().set(request.getSessionId(), cart);
	}
	/// 비회원/회원일 때 장바구니 아이템 생성 메소드
	private void createCartItemForCustomer(RequestAddCartItemsDTO request) {
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
	 * 장바구니 항목 수량 수정 메소드
	 */
	@Override
	public void updateCartItem(long cartItemId, RequestUpdateCartItemsDTO request) {
		if (Objects.nonNull(request.getSessionId())) {
			updateCartItemForGuest(cartItemId, request);
		} else {
			updateCartItemForCustomer(cartItemId, request);
		}
	}
	/// 게스트일 때 장바구니 항목 수량 변경 메소드
	private void updateCartItemForGuest(long cartItemId, RequestUpdateCartItemsDTO request) {
		Cart cart = (Cart) redisTemplate.opsForValue().get(request.getSessionId());
		if (Objects.isNull(cart)) {
			throw new CartNotFoundException("Cart not found in session");
		}

		// 장바구니 항목 찾기 및 수량 변경
		CartItems cartItem = cart.getCartItems().stream()
			.filter(item -> item.getCartItemsId() == cartItemId)
			.findFirst()
			.orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

		cartItem.changeCartItemsQuantity(request.getQuantity());

		// 변경된 Cart 객체를 Redis에 저장
		redisTemplate.opsForValue().set(request.getSessionId(), cart);
	}
	/// 비회원/회원일 때 장바구니 아이템 수량 변경 메소드
	private void updateCartItemForCustomer(long cartItemId, RequestUpdateCartItemsDTO request) {
		// 장바구니 항목 존재 검증
		CartItems findCartItem = cartItemsRepository.findById(cartItemId)
			.orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

		// 해당 장바구니 아이템의 수량 변경
		findCartItem.changeCartItemsQuantity(request.getQuantity());
	}

	/**
	 * 장바구니 항목 삭제 메소드
	 */
	@Override
	public void deleteCartItem(long cartItemId, RequestDeleteCartItemsDTO request) {
		if (Objects.nonNull(request.getSessionId())) {
			deleteCartItemForQuest(cartItemId, request);
		} else {
			deleteCartItemForCustomer(cartItemId);
		}
	}
	/// 게스트일 때 장바구니 항목 삭제 메소드
	private void deleteCartItemForQuest(long cartItemId, RequestDeleteCartItemsDTO request) {
		Cart cart = (Cart) redisTemplate.opsForValue().get(request.getSessionId());
		if (cart == null) {
			throw new CartItemNotFoundException("Cart not found in session");
		}

		// 해당 항목 삭제
		cart.getCartItems().removeIf(item -> item.getCartItemsId() == cartItemId);

		// 삭제된 Cart 객체를 Redis에 저장
		redisTemplate.opsForValue().set(request.getSessionId(), cart);
	}
	/// 비회원/회원일 때 장바구니 아이템 삭제 메소드
	private void deleteCartItemForCustomer(long cartItemId) {
		/// 장바구니 항목 존재 검증
		CartItems findCartItem = cartItemsRepository.findById(cartItemId)
			.orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

		// 해당 장바구니 아이템 삭제
		cartItemsRepository.delete(findCartItem);
	}

	/**
	 * 고객의 장바구니 페이징 목록 조회 메소드
	 */
	@Override
	public Page<ResponseCartItemsDTO> getCartItemsByCustomer(long customerId, Pageable pageable) {
		// 고객이 담은 장바구니들을 페이지로 담음
		Page<CartItems> cartItemsPage = cartItemsRepository.findByCart_Customer_CustomerId(customerId, pageable);

		// 필요한 api 스펙에 맞춰 response 재가공해서 반환
		return cartItemsPage.map(cartItem -> {
			Product product = cartItem.getProduct();
			String productImagePath = "default.jpg";

			if (Objects.nonNull(product.getProductImage())) {
				productImagePath = product.getProductImage().getFirst().getProductImagePath();
			}

			return new ResponseCartItemsDTO(
				cartItem.getCartItemsId(),
				product.getProductId(),
				product.getProductTitle(),
				product.getProductSalePrice(),
				productImagePath,
				cartItem.getCartItemsQuantity());
		});
	}

}
