package com.nhnacademy.back.cart.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.cart.domain.dto.CartDTO;
import com.nhnacademy.back.cart.domain.dto.CartItemDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestAddCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartItemsForMemberDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestDeleteCartOrderDTO;
import com.nhnacademy.back.cart.domain.dto.request.RequestUpdateCartItemsDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForGuestDTO;
import com.nhnacademy.back.cart.domain.dto.response.ResponseCartItemsForMemberDTO;
import com.nhnacademy.back.cart.exception.CartAlreadyExistsException;
import com.nhnacademy.back.cart.exception.CartItemNotFoundException;
import com.nhnacademy.back.cart.exception.CartNotFoundException;
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

	private final ProductJpaRepository productRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;


	private static final String MEMBER_HASH_NAME = "MemberCart:";
	private static final String GUEST_HASH_NAME = "GuestCart:";

	/**
	 * 회원 장바구니 생성 메소드
	 */
	@Override
	public void createCartForMember(String memberId) {
		if (redisTemplate.opsForHash().hasKey(MEMBER_HASH_NAME, memberId)) {
			throw new CartAlreadyExistsException();
		}

		CartDTO newCart = new CartDTO();
		redisTemplate.opsForHash().put(MEMBER_HASH_NAME, memberId, newCart);
	}

	/**
	 * 회원일 때 장바구니 항목 생성 메소드
	 */
	@Override
	public int createCartItemForMember(RequestAddCartItemsDTO request) {
		// 장바구니 조회
		CartDTO cart;
		if (redisTemplate.opsForHash().hasKey(MEMBER_HASH_NAME, request.getMemberId())) {
			Object o = redisTemplate.opsForHash().get(MEMBER_HASH_NAME, request.getMemberId());
			cart = objectMapper.convertValue(o, CartDTO.class);
		} else {
			cart = new CartDTO();
		}

		// 상품 조회
		Product findProduct = productRepository.findById(request.getProductId())
			.orElseThrow(ProductNotFoundException::new);
		// 상품 상태 검증
		if (findProduct.getProductState().getProductStateId() != 1) {
			throw new ProductNotForSaleException("현재 판매중인 상품이 아닙니다.");
		}

		// 현재 고객이 장바구니 아이템을 가지고 있을 경우 병합
		Optional<CartItemDTO> existingItemOpt = cart.getCartItems().stream()
			.filter(item -> item.getProductId() == findProduct.getProductId())
			.findFirst();
		// 존재하면 수량 누적
		if (existingItemOpt.isPresent()) {
			CartItemDTO existingItem = existingItemOpt.get();
			existingItem.setCartItemsQuantity(existingItem.getCartItemsQuantity() + request.getQuantity());

			redisTemplate.opsForHash().put(MEMBER_HASH_NAME, request.getMemberId(), cart);
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
		redisTemplate.opsForHash().put(MEMBER_HASH_NAME, request.getMemberId(), cart);

		return cart.getCartItems().size();
	}

	/**
	 * 회원일 때 장바구니 항목 수량 수정 메소드
	 */
	@Override
	public int updateCartItemForMember(RequestUpdateCartItemsDTO request) {
		Object o = redisTemplate.opsForHash().get(MEMBER_HASH_NAME, request.getMemberId());
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
		redisTemplate.opsForHash().put(MEMBER_HASH_NAME, request.getMemberId(), cart);

		return cart.getCartItems().size();
	}

	/**
	 * 회원일 때 장바구니 항목 삭제 메소드
	 */
	@Override
	public void deleteCartItemForMember(RequestDeleteCartItemsForMemberDTO request) {
		// 장바구니 조회
		Object o = redisTemplate.opsForHash().get(MEMBER_HASH_NAME, request.getMemberId());
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			throw new CartItemNotFoundException();
		}

		// 해당 항목 삭제
		cart.getCartItems().removeIf(item -> item.getProductId() == request.getProductId());

		// 삭제된 Cart 객체를 Redis에 저장
		redisTemplate.opsForHash().put(MEMBER_HASH_NAME, request.getMemberId(), cart);
	}

	/**
	 * 회원일 때 장바구니 전체 삭제 메소드
	 */
	@Transactional
	@Override
	public void deleteCartForMember(String memberId) {
		redisTemplate.opsForHash().delete(MEMBER_HASH_NAME, memberId);
	}

	/**
	 * 회원인 고객의 장바구니 목록 조회 메소드
	 */
	@Override
	public List<ResponseCartItemsForMemberDTO> getCartItemsByMember(String memberId) {
		// 고객이 담은 장바구니들을 리스트로 담음
		Object o = redisTemplate.opsForHash().get(MEMBER_HASH_NAME, memberId);
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			return new ArrayList<>();
		}

		List<CartItemDTO> cartItems = cart.getCartItems();

		// 필요한 api 스펙에 맞춰 response 재가공해서 반환
		return cartItems.stream()
			.map(cartItem -> {
				// 해당 상품 조회 및 검증
				Product product = productRepository.findById(cartItem.getProductId())
					.orElseThrow(ProductNotFoundException::new);

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
		Object o = redisTemplate.opsForHash().get(MEMBER_HASH_NAME, memberId);
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			return 0;
		}

		return cart.getCartItems().size();
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
		Object o = redisTemplate.opsForHash().get(GUEST_HASH_NAME, request.getSessionId());
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
		redisTemplate.opsForHash().put(GUEST_HASH_NAME, request.getSessionId(), cart);

		return cart.getCartItems().size();
	}

	/**
	 * 게스트일 때 장바구니 항목 수량 변경 메소드
	 */
	@Override
	public int updateCartItemForGuest(RequestUpdateCartItemsDTO request) {
		Object o = redisTemplate.opsForHash().get(MEMBER_HASH_NAME, request.getSessionId());
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
		redisTemplate.opsForHash().put(GUEST_HASH_NAME, request.getSessionId(), cart);

		return cart.getCartItems().size();
	}

	/**
	 * 게스트일 때 장바구니 항목 삭제 메소드
	 */
	@Override
	public void deleteCartItemForGuest(RequestDeleteCartItemsForGuestDTO request) {
		Object o = redisTemplate.opsForHash().get(GUEST_HASH_NAME, request.getSessionId());
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			throw new CartItemNotFoundException();
		}

		// 해당 항목 삭제
		cart.getCartItems().removeIf(item -> item.getProductId() == request.getProductId());

		// 삭제된 Cart 객체를 Redis에 저장
		redisTemplate.opsForHash().put(GUEST_HASH_NAME, request.getSessionId(), cart);
	}

	/**
	 * 게스트일 때 장바구니 항목 전체 삭제 메소드
	 */
	@Override
	public void deleteCartForGuest(String sessionId) {
		Object o = redisTemplate.opsForHash().get(GUEST_HASH_NAME, sessionId);
		CartDTO cart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(cart)) {
			throw new CartItemNotFoundException();
		}

		redisTemplate.opsForHash().delete(GUEST_HASH_NAME, sessionId);
	}

	/**
	 * 게스트인 고객의 장바구니 목록 조회
	 */
	@Override
	public List<ResponseCartItemsForGuestDTO> getCartItemsByGuest(String sessionId) {
		Object o = redisTemplate.opsForHash().get(GUEST_HASH_NAME, sessionId);
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
		// 회원 장바구니 확인
		CartDTO memberCart;
		if (redisTemplate.opsForHash().hasKey(MEMBER_HASH_NAME, memberId)) {
			Object o = redisTemplate.opsForHash().get(MEMBER_HASH_NAME, memberId);
			memberCart = objectMapper.convertValue(o, CartDTO.class);
		} else {
			memberCart = new CartDTO();
		}

		// 게스트 장바구니 확인 
		Object o = redisTemplate.opsForHash().get(GUEST_HASH_NAME, sessionId);
		CartDTO guestCart = objectMapper.convertValue(o, CartDTO.class);
		if (Objects.isNull(guestCart)) {
			return memberCart.getCartItems().size();
		}

		// 게스트 장바구니가 비어 있지 않은 경우 병합 수행
		if (Objects.nonNull(guestCart.getCartItems())) {
			Map<Long, CartItemDTO> memberItemMap = new HashMap<>();

			// 회원 장바구니의 기존 항목을 Map 에 담아 중복 검출 용이하게 구성
			for (CartItemDTO item : memberCart.getCartItems()) {
				memberItemMap.put(item.getProductId(), item);
			}

			for (CartItemDTO guestItem : guestCart.getCartItems()) {
				CartItemDTO existingItem = memberItemMap.get(guestItem.getProductId());
				if (existingItem != null) {
					// 중복 상품은 수량만 증가
					existingItem.setCartItemsQuantity(existingItem.getCartItemsQuantity() + guestItem.getCartItemsQuantity());
				} else {
					// 새로운 상품은 회원 장바구니에 추가
					memberCart.getCartItems().add(guestItem);
					memberItemMap.put(guestItem.getProductId(), guestItem);
				}
			}

			// 병합 후: Redis에 회원 장바구니 저장
			redisTemplate.opsForHash().put(MEMBER_HASH_NAME, memberId, memberCart);
		}

		// 병합 완료 후, 게스트 장바구니 삭제
		redisTemplate.opsForHash().delete(GUEST_HASH_NAME, sessionId);

		// 최종 회원 장바구니 항목 개수 반환
		return memberCart.getCartItems().size();
	}

	/**
	 * 주문 완료한 상품 항목 장바구니에 수량 변경 또는 삭제 업데이트
	 */
	@Override
	public Integer deleteOrderCompleteCartItems(RequestDeleteCartOrderDTO requestOrderCartDeleteDTO) {
		Object o;
		CartDTO orderCart;

		// 회원인 경우
		if (isMember(requestOrderCartDeleteDTO)) {
			o = redisTemplate.opsForHash().get(MEMBER_HASH_NAME, requestOrderCartDeleteDTO.getMemberId());
			orderCart = objectMapper.convertValue(o, CartDTO.class);
			return deleteOrderComplete(MEMBER_HASH_NAME, orderCart, requestOrderCartDeleteDTO);
		}
		
		// 게스트인 경우
		o = redisTemplate.opsForHash().get(GUEST_HASH_NAME, requestOrderCartDeleteDTO.getMemberId());
		orderCart = objectMapper.convertValue(o, CartDTO.class);
		return deleteOrderComplete(GUEST_HASH_NAME, orderCart, requestOrderCartDeleteDTO);
	}

	// 회원 여부 확인
	private boolean isMember(RequestDeleteCartOrderDTO requestOrderCartDeleteDTO) {
		return !StringUtils.isEmpty(requestOrderCartDeleteDTO.getMemberId());
	}
	
	// 장바구니 삭제 메소드
	private Integer deleteOrderComplete(String hashName, CartDTO orderCart, RequestDeleteCartOrderDTO requestOrderCartDeleteDTO) {
		if (Objects.nonNull(orderCart) && Objects.nonNull(orderCart.getCartItems())) {
			// CartItems 필터링 : CartItems에서 특정 상품들을 제외한 새로운 리스트 생성
			List<CartItemDTO> filteredItems = orderCart.getCartItems().stream()
				.filter(item -> requestOrderCartDeleteDTO.getProductIds().stream().noneMatch(id -> id.equals(item.getProductId())))
				.toList();

			// 기존 장바구니에 삭제된 항목 갱신된 리스트로 적용
			orderCart.setCartItems(filteredItems);
			
			// 삭제한 cart로 갱신
			redisTemplate.opsForHash().put(hashName, requestOrderCartDeleteDTO.getMemberId(), orderCart);
			return orderCart.getCartItems().size();
		}

		return 0;
	}

}
