package com.nhnacademy.back.batch.birthday;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BirthdayCouponRabbitConfig {
	/**
	 * RabbitAdmin 이라는 Spring 컴포넌트가 queue, exchange, binding 을 자동으로 MQ 서버에 등록
	 */

	public static final String BIRTHDAY_EXCHANGE = "E2BS.exchange";
	public static final String BIRTHDAY_QUEUE = "E2BS.birthday.coupon.queue";
	public static final String BIRTHDAY_ROUTING_KEY = "E2BS.birthday.coupon.key";

	@Bean
	public Queue birthdayQueue() {
		return new Queue(BIRTHDAY_QUEUE, true);
	}

	@Bean
	public DirectExchange birthdayExchange() {
		return new DirectExchange(BIRTHDAY_EXCHANGE);
	}

	@Bean
	public Binding birthdayBinding() {
		return BindingBuilder
			.bind(birthdayQueue())
			.to(birthdayExchange())
			.with(BIRTHDAY_ROUTING_KEY);
	}
}

