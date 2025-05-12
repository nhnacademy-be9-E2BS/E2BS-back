// package com.nhnacademy.back.common.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
// import org.springframework.data.redis.core.RedisTemplate;

// @Configuration
// public class RedisTestConfig {

// 	@Bean
// 	public RedisTemplate<String, Object> redisTemplate() {
// 		RedisTemplate<String, Object> template = new RedisTemplate<>();
// 		template.setConnectionFactory(jedisConnectionFactory());
// 		return template;
// 	}

// 	@Bean
// 	public JedisConnectionFactory jedisConnectionFactory() {
// 		JedisConnectionFactory factory = new JedisConnectionFactory();
// 		factory.setHostName("localhost");
// 		factory.setPort(6379);
// 		return factory;
// 	}

// }
