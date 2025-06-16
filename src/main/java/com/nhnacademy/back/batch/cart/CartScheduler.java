package com.nhnacademy.back.batch.cart;

import java.util.Objects;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.cart.domain.dto.CartDTO;
import com.nhnacademy.back.cart.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartScheduler {

	private final CartService cartService;

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;
	private static final String MEMBER_HASH_NAME = "MemberCart:";

	/**
	 * 일정 주기마다 모든 회원의 Redis 장바구니를 DB에 업데이트
	 */
	@Scheduled(fixedRate = 60 * 60 * 1000) // 1시간 마다
	@Transactional
	public void syncCartToDatabase() {
		try {
			Set<Object> memberIds = redisTemplate.opsForHash().keys(MEMBER_HASH_NAME);

			if (memberIds.isEmpty()) {
				log.info("동기화 스킵 - Redis에 아무 장바구니도 없음");
				return;
			}

			for (Object memberIdObject : memberIds) {
				String memberId = String.valueOf(memberIdObject);
				try {
					Object o = redisTemplate.opsForHash().get(MEMBER_HASH_NAME, memberId);
					if (Objects.isNull(o)) {
						continue;
					}

					CartDTO cartDTO = objectMapper.convertValue(o, CartDTO.class);
					if (cartDTO.getCartItems().isEmpty()) {
						continue;
					}

					cartService.saveCartItemsDBFromRedis(memberId, cartDTO.getCartItems());

					log.info("Redis → DB 장바구니 동기화 완료 - memberId: {}", memberId);
				} catch (Exception e) {
					log.warn("memberId: {} 동기화 중 오류 발생", memberId, e);
				}
			}

		} catch (Exception e) {
			log.error("전체 Redis 장바구니 동기화 실패", e);
		}
	}
	
}
