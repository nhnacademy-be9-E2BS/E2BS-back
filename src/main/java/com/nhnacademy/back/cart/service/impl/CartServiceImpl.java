package com.nhnacademy.back.cart.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.exception.CustomerNotFoundException;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.cart.domain.dto.CartDTO;
import com.nhnacademy.back.cart.domain.dto.CartItemDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartOrderDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForMemberDTO;
import com.nhnacademy.back.cart.domain.entity.Cart;
import com.nhnacademy.back.cart.domain.entity.CartItems;
import com.nhnacademy.back.cart.exception.CartItemNotFoundException;
import com.nhnacademy.back.cart.exception.CartNotFoundException;
import com.nhnacademy.back.cart.repository.CartItemsJpaRepository;
import com.nhnacademy.back.cart.repository.CartJpaRepository;
import com.nhnacademy.back.cart.service.CartService;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotForSaleException;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CustomerJpaRepository customerRepository;
	private final MemberJpaRepository memberRepository;
	private final ProductJpaRepository productRepository;
	private final CartJpaRepository cartRepository;
	private final CartItemsJpaRepository cartItemsRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	private static final String NOT_FOUND_MEMBER = "아이디에 해당하는 회원을 찾지 못했습니다.";

	/**
	 * 회원일 때 장바구니 항목 생성 메소드
	 */
	@Transactional
	@Override
	public int createCartItemForMember(RequestAddCartItemsDTO request) {
		// 비회원/회원, 상품 존재 검증
		Member findMember = memberRepository.getMemberByMemberId(request.getMemberId());
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException(NOT_FOUND_MEMBER);
		}
		Customer findCustomer = customerRepository.findById(findMember.getCustomerId())
			.orElseThrow(CustomerNotFoundException::new);
		Product findProduct = productRepository.findById(request.getProductId())
			.orElseThrow(ProductNotFoundException::new);
		// 상품 상태 검증
		if (findProduct.getProductState().getProductStateId() != 1) {
			throw new ProductNotForSaleException("현재 판매중인 상품이 아닙니다.");
		}

		Cart cart;
		// 장바구니가 없으면 장바구니 생성
		if (!cartRepository.existsByCustomer_CustomerId(findCustomer.getCustomerId())) {
			cart = cartRepository.save(new Cart(findCustomer));
		} else {
			cart = cartRepository.findByCustomer_CustomerId(findCustomer.getCustomerId())
				.orElseThrow(CartNotFoundException::new);
		}

		// 현재 고객이 장바구니 아이템을 가지고 있을 경우 병합
		if (cartItemsRepository.existsByCartAndProduct(cart, findProduct)) {
			CartItems findCartItem = cartItemsRepository.findByCartAndProduct(cart, findProduct)
				.orElseThrow(CartItemNotFoundException::new);

			findCartItem.changeCartItemsQuantity(findCartItem.getCartItemsQuantity() + request.getQuantity());
			return cart.getCartItems().size();
		}

		// 장바구니 아이템 생성
		cartItemsRepository.save(new CartItems(cart, findProduct, request.getQuantity()));

		return cart.getCartItems().size();
	}

	/**
	 * 회원일 때 장바구니 항목 수량 수정 메소드
	 */
	@Transactional
	@Override
	public int updateCartItemForMember(long cartItemId, RequestUpdateCartItemsDTO request) {
		Member findMember = memberRepository.getMemberByMemberId(request.getMemberId());
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException(NOT_FOUND_MEMBER);
		}

		Cart findCart = cartRepository.findByCustomer_CustomerId(findMember.getCustomerId())
			.orElseThrow(CartNotFoundException::new);

		// 장바구니 항목 존재 검증
		CartItems findCartItem = cartItemsRepository.findById(cartItemId)
			.orElseThrow(CartItemNotFoundException::new);

		// 해당 장바구니 아이템의 수량 변경
		if (request.getQuantity() > 0) {
			findCartItem.changeCartItemsQuantity(request.getQuantity());
			return findCart.getCartItems().size();
		} else {
			cartItemsRepository.delete(findCartItem);
			findCart.getCartItems().remove(findCartItem);
			return getCartItemsCountsForMember(findMember.getMemberId());
		}
	}

	/**
	 * 회원일 때 장바구니 항목 삭제 메소드
	 */
	@Transactional
	@Override
	public void deleteCartItemForMember(long cartItemId) {
		// 장바구니 항목 존재 검증
		CartItems findCartItem = cartItemsRepository.findById(cartItemId)
			.orElseThrow(CartItemNotFoundException::new);

		// 해당 장바구니 아이템 삭제
		cartItemsRepository.delete(findCartItem);
	}

	/**
	 * 회원일 때 장바구니 전체 삭제 메소드
	 */
	@Transactional
	@Override
	public void deleteCartForMember(String memberId) {
		Member findMember = memberRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException(NOT_FOUND_MEMBER);
		}

		Cart findCart = cartRepository.findByCustomer_CustomerId(findMember.getCustomerId())
			.orElseThrow(CartNotFoundException::new);

		if (Objects.isNull(findCart)) {
			throw new CartNotFoundException();
		}

		cartRepository.delete(findCart);
	}

	/**
	 * 회원인 고객의 장바구니 목록 조회 메소드
	 */
	@Override
	public List<ResponseCartItemsForMemberDTO> getCartItemsByMember(String memberId) {
		// 고객이 담은 장바구니들을 리스트로 담음
		Member findMember = memberRepository.getMemberByMemberId(memberId);
		List<CartItems> cartItems = cartItemsRepository.findByCart_Customer_CustomerId(findMember.getCustomerId());

		// 필요한 api 스펙에 맞춰 response 재가공해서 반환
		return cartItems.stream()
			.map(cartItem -> {
				// 해당 상품 조회 및 검증
				Product product = cartItem.getProduct();
				if (Objects.isNull(product)) {
					throw new ProductNotFoundException();
				}

				// 기본 이미지 설정
				String productImagePath = "default.jpg";

				// 해당 상품의 이미지 검증
				if (Objects.nonNull(product.getProductImage()) && !product.getProductImage().isEmpty()) {
					productImagePath = product.getProductImage().getFirst().getProductImagePath();
				}

				//  DTO 가공
				long productRegularPrice = product.getProductRegularPrice();
				long productSalePrice = product.getProductSalePrice();
				BigDecimal regularPrice = BigDecimal.valueOf(productRegularPrice);
				BigDecimal salePrice = BigDecimal.valueOf(productSalePrice);

				BigDecimal discountRate = BigDecimal.ZERO;
				if (regularPrice.compareTo(BigDecimal.ZERO) > 0) {
					discountRate = regularPrice.subtract(salePrice)         // 할인 금액
						.divide(regularPrice, 2, RoundingMode.HALF_UP) // 정가로 나눠서 비율
						.multiply(BigDecimal.valueOf(100))                  // % 변환
						.setScale(0, RoundingMode.DOWN);            // 소수점 제거
				}

				long productTotalPrice = product.getProductSalePrice() * cartItem.getCartItemsQuantity();

				return new ResponseCartItemsForMemberDTO(
					cartItem.getCartItemsId(),
					product.getProductId(),
					product.getProductTitle(),
					productRegularPrice,
					productSalePrice,
					discountRate,
					productImagePath,
					cartItem.getCartItemsQuantity(),
					productTotalPrice);
			})
			.toList();
	}

	/**
	 * 회원일 때 장바구니 항목 개수 조회
	 */
	@Override
	public Integer getCartItemsCountsForMember(String memberId) {
		Member findMember = memberRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException(NOT_FOUND_MEMBER);
		}

		Optional<Cart> findCart = cartRepository.findByCustomer_CustomerId(findMember.getCustomerId());
		return findCart.map(cart -> cart.getCartItems().size()).orElse(0);
	}


	/**
	 * 게스트일 때 장바구니 항목 생성 메소드
	 */
	@Override
	public int createCartItemForGuest(RequestAddCartItemsDTO request) {
		// 상품 존재 검증
		Product findProduct = productRepository.findById(request.getProductId())
			.orElseThrow(ProductNotFoundException::new);
		// 상품 상태 검증
		if (findProduct.getProductState().getProductStateId() != 1) {
			throw new ProductNotForSaleException("현재 판매중인 상품이 아닙니다.");
		}

		// Redis에서 장바구니 가져오기 (없으면 생성)
		Object o = redisTemplate.opsForValue().get(request.getSessionId());
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			cart = new CartDTO();
		}

		Optional<CartItemDTO> existingItemOpt = cart.getCartItems().stream()
			.filter(item -> item.getProductId() == findProduct.getProductId())
			.findFirst();
		// 존재하면 수량 누적
		if (existingItemOpt.isPresent()) {
			CartItemDTO existingItem = existingItemOpt.get();
			existingItem.setCartItemsQuantity(existingItem.getCartItemsQuantity() + request.getQuantity());

			redisTemplate.opsForValue().set(request.getSessionId(), cart);
			return cart.getCartItems().size();
		}

		// 장바구니 항목 생성 및 Cart에 추가
		String productImagePath = "default.jpg";
		if (Objects.nonNull(findProduct.getProductImage()) && !findProduct.getProductImage().isEmpty()) {
			productImagePath = findProduct.getProductImage().getFirst().getProductImagePath();
		}

		long productRegularPrice = findProduct.getProductRegularPrice();
		long productSalePrice = findProduct.getProductSalePrice();
		BigDecimal regularPrice = BigDecimal.valueOf(productRegularPrice);
		BigDecimal salePrice = BigDecimal.valueOf(productSalePrice);

		// 할인률 계산: ((정가 - 할인가) / 정가) * 100
		BigDecimal discountRate = BigDecimal.ZERO;
		if (regularPrice.compareTo(BigDecimal.ZERO) > 0) {
			discountRate = regularPrice.subtract(salePrice)         // 할인 금액
				.divide(regularPrice, 2, RoundingMode.HALF_UP) // 정가로 나눠서 비율
				.multiply(BigDecimal.valueOf(100))                  // % 변환
				.setScale(0, RoundingMode.DOWN);            // 소수점 제거
		}

		CartItemDTO newItem = new CartItemDTO(
			findProduct.getProductId(),
			findProduct.getProductTitle(),
			productRegularPrice,
			productSalePrice,
			discountRate,
			productImagePath,
			request.getQuantity()
		);
		cart.getCartItems().add(newItem);

		// 변경된 Cart 객체를 Redis에 저장
		redisTemplate.opsForValue().set(request.getSessionId(), cart);

		return cart.getCartItems().size();
	}

	/**
	 * 게스트일 때 장바구니 항목 수량 변경 메소드
	 */
	@Override
	public int updateCartItemForGuest(RequestUpdateCartItemsDTO request) {
		Object o = redisTemplate.opsForValue().get(request.getSessionId());
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			throw new CartNotFoundException();
		}

		// 장바구니 항목 찾기 및 수량 변경
		CartItemDTO cartItem = cart.getCartItems().stream()
			.filter(item -> Objects.equals(item.getProductId(), request.getProductId()))
			.findFirst()
			.orElseThrow(CartItemNotFoundException::new);

		cartItem.setCartItemsQuantity(request.getQuantity());

		// 해당 항목이 0개일 때 삭제
		if (cartItem.getCartItemsQuantity() == 0) {
			cart.getCartItems().removeIf(item -> item.getProductId() == request.getProductId());
		}

		// 변경된 Cart 객체를 Redis에 저장
		redisTemplate.opsForValue().set(request.getSessionId(), cart);

		return cart.getCartItems().size();
	}

	/**
	 * 게스트일 때 장바구니 항목 삭제 메소드
	 */
	@Override
	public void deleteCartItemForGuest(RequestDeleteCartItemsForGuestDTO request) {
		Object o = redisTemplate.opsForValue().get(request.getSessionId());
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			throw new CartItemNotFoundException();
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
			throw new CartItemNotFoundException();
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
			.map(cartItem -> {
				long productTotalPrice = cartItem.getProductSalePrice() * cartItem.getCartItemsQuantity();
				return new ResponseCartItemsForGuestDTO(
				cartItem.getProductId(),
				cartItem.getProductTitle(),
				cartItem.getProductRegularPrice(),
				cartItem.getProductSalePrice(),
				cartItem.getDiscountRate(),
				cartItem.getProductImagePath(),
				cartItem.getCartItemsQuantity(),
				productTotalPrice);
			})
			.toList();
	}

	/**
	 * 게스트 장바구니 -> 회원 장바구니와 병합 메소드
	 */
	@Transactional
	@Override
	public Integer mergeCartItemsToMemberFromGuest(String memberId, String sessionId) {
		// 회원 검증
		Member findMember = memberRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(findMember)) {
			throw new NotFoundMemberException(NOT_FOUND_MEMBER);
		}
		Customer findCustomer = customerRepository.findById(findMember.getCustomerId())
			.orElseThrow(CustomerNotFoundException::new);

		// 장바구니 검증
		Cart cart;
		// 장바구니가 없으면 장바구니 생성
		if (!cartRepository.existsByCustomer_CustomerId(findCustomer.getCustomerId())) {
			cart = cartRepository.save(new Cart(findCustomer));
		} else {
			cart = cartRepository.findByCustomer_CustomerId(findCustomer.getCustomerId())
				.orElseThrow(CartNotFoundException::new);
		}

		// 게스트 장바구니 확인 
		Object o = redisTemplate.opsForValue().get(sessionId);
		CartDTO redisCart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(redisCart)) {
			return cart.getCartItems().size();
		}

		// 병합
		List<CartItemDTO> redisCartItems = redisCart.getCartItems();
		for (CartItemDTO cartItem : redisCartItems) {
			long productId = cartItem.getProductId();

			Product findProduct = productRepository.findById(productId)
				.orElseThrow(ProductNotFoundException::new);

			// 현재 고객이 장바구니 아이템을 가지고 있을 경우 수량만 올려주기
			if (cartItemsRepository.existsByCartAndProduct(cart, findProduct)) {
				CartItems findCartItem = cartItemsRepository.findByCartAndProduct(cart, findProduct)
					.orElseThrow(CartItemNotFoundException::new);

				findCartItem.changeCartItemsQuantity(findCartItem.getCartItemsQuantity() + cartItem.getCartItemsQuantity());
			} else {
				// 현재 고객이 장바구니 아이템을 가지고 있지 않을 경우 새로 저장
				cartItemsRepository.save(new CartItems(cart, findProduct, cartItem.getCartItemsQuantity()));
			}
		}

		return cart.getCartItems().size();
	}

	/**
	 * 주문 완료한 상품 항목 장바구니에 수량 변경 또는 삭제 업데이트
	 */
	@Transactional
	@Override
	public Integer deleteOrderCompleteCartItems(RequestDeleteCartOrderDTO requestOrderCartDeleteDTO) {
		List<Long> productIds = requestOrderCartDeleteDTO.getProductIds();
		List<Integer> cartQuantities = requestOrderCartDeleteDTO.getCartQuantities();

		// 회원인 경우는 DB에 이미 수량이 더 존재할 수 있으므로 수량 업데이트
		if (!StringUtils.isEmpty(requestOrderCartDeleteDTO.getMemberId())) {
			Member findMember = memberRepository.getMemberByMemberId(requestOrderCartDeleteDTO.getMemberId());
			if (Objects.isNull(findMember)) {
				throw new NotFoundMemberException(NOT_FOUND_MEMBER);
			}

			Cart findCart = cartRepository.findByCustomer_CustomerId(findMember.getCustomerId())
				.orElseThrow(CartNotFoundException::new);

			List<CartItems> cartItems = findCart.getCartItems();

			// 현재 cartItems 리스트를 순회 중인데 동시에 remove 도 일어나므로 ConcurrentModificationException 발생 방지를 위해 리스트 복사
			List<CartItems> copiedCartItems = new ArrayList<>(cartItems);
			for (CartItems cartItem : copiedCartItems) {
				for (int i = 0; i < productIds.size(); i++) {
					if (cartItem.getProduct().getProductId() == productIds.get(i)) {
						int newQuantity = cartItem.getCartItemsQuantity() - cartQuantities.get(i) * 2;

						if (newQuantity > 0) {
							cartItem.changeCartItemsQuantity(newQuantity);
						} else {
							cartItemsRepository.delete(cartItem);
							cartItems.remove(cartItem); // 관계 정리
						}
						break;
					}
				}
			}

			return getCartItemsCountsForMember(findMember.getMemberId());
		} else { // 비회원인 경우는 삭제만 처리하면 됨
			Object o = redisTemplate.opsForValue().get(requestOrderCartDeleteDTO.getSessionId());
			CartDTO cart = objectMapper.convertValue(o, CartDTO.class);

			// 기존 cartItems
			List<CartItemDTO> cartItems = cart.getCartItems();

			// 기존 cartItems 중에서 productIds에 포함되지 않은 상품들만 남긴 새로운 리스트를 가공
			List<CartItemDTO> filteredItems = cartItems.stream()
				.filter(item -> productIds.stream().noneMatch(id -> id.equals(item.getProductId())))
				.collect(Collectors.toList());

			cart.setCartItems(filteredItems); // 새로운 리스트로 덮어씌우기

			return cart.getCartItems().size();
		}
	}

}
