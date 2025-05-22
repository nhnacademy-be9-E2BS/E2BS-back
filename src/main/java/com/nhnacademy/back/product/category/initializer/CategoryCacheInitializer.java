package com.nhnacademy.back.product.category.initializer;

import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class CategoryCacheInitializer implements ApplicationRunner {

	private final RedisTemplate<String, Object> redisTemplate;

	public CategoryCacheInitializer(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void run(ApplicationArguments args) {
		Set<String> keys = redisTemplate.keys("Categories::*");
		if (!keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
	}
}

