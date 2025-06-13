package com.nhnacademy.back.product.category;

import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.data.redis.core.RedisTemplate;

import com.nhnacademy.back.product.category.initializer.CategoryCacheInitializer;

class CategoryCacheInitializerTest {

	@Test
	@DisplayName("Redis에 해당 키들이 있을 경우 삭제되는지 확인")
	void testRun_keysExist_shouldDeleteKeys() {
		// given
		RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
		Set<String> dummyKeys = Set.of("Categories::1", "Categories::2");

		when(redisTemplate.keys("Categories::*")).thenReturn(dummyKeys);

		CategoryCacheInitializer initializer = new CategoryCacheInitializer(redisTemplate);

		// when
		initializer.run(mock(ApplicationArguments.class));

		// then
		verify(redisTemplate).delete(dummyKeys);
	}

	@Test
	@DisplayName("Redis에 해당 키들이 없을 경우 아무 동작도 하지 않음")
	void testRun_noKeys_shouldDoNothing() {
		// given
		RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);

		when(redisTemplate.keys("Categories::*")).thenReturn(Set.of());

		CategoryCacheInitializer initializer = new CategoryCacheInitializer(redisTemplate);

		// when
		initializer.run(mock(ApplicationArguments.class));

		// then
		verify(redisTemplate, never()).delete(any(Set.class));
	}

	@Test
	@DisplayName("Redis keys()가 null 반환할 경우도 안전하게 동작")
	void testRun_keysNull_shouldDoNothing() {
		// given
		RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);

		when(redisTemplate.keys("Categories::*")).thenReturn(null);

		CategoryCacheInitializer initializer = new CategoryCacheInitializer(redisTemplate);

		// when
		initializer.run(mock(ApplicationArguments.class));

		// then
		verify(redisTemplate, never()).delete(any(Set.class));
	}
}
