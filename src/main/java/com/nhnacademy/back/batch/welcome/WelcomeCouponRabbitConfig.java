package com.nhnacademy.back.batch.welcome;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WelcomeCouponRabbitConfig {
	/**
	 * RabbitAdmin 이라는 Spring 컴포넌트가 queue, exchange, binding 을 자동으로 MQ 서버에 등록
	 */

	public static final String WELCOME_EXCHANGE = "E2BS.exchange";
	public static final String WELCOME_QUEUE = "E2BS.welcome.coupon.queue";
	public static final String WELCOME_ROUTING_KEY = "E2BS.welcome.coupon.key";

	@Bean
	public Queue welcomeQueue() {
		return new Queue(WELCOME_QUEUE, true);
	}

	@Bean
	public DirectExchange welcomeExchange() {
		return new DirectExchange(WELCOME_EXCHANGE);
	}

	@Bean
	public Binding adminBinding() {
		return BindingBuilder
			.bind(welcomeQueue())
			.to(welcomeExchange())
			.with(WELCOME_ROUTING_KEY);
	}
}

