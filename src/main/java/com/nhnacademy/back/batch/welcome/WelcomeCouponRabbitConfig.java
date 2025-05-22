package com.nhnacademy.back.batch.welcome;

import java.util.HashMap;
import java.util.Map;

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

	public static final String WELCOME_DLX = "E2BS.exchange.dlx";
	public static final String WELCOME_DLQ = "E2BS.welcome.coupon.queue.dlq";
	public static final String WELCOME_DLK = "E2BS.welcome.coupon.key.dlk";

	@Bean
	public Queue welcomeDLQ() {
		return new Queue(WELCOME_DLQ, true);
	}

	@Bean
	public DirectExchange welcomeDLX() {
		return new DirectExchange(WELCOME_DLX);
	}

	@Bean
	public Binding adminDLKBinding() {
		return BindingBuilder
			.bind(welcomeDLQ())
			.to(welcomeDLX())
			.with(WELCOME_DLK);
	}

	@Bean
	public Queue welcomeQueue() {
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-dead-letter-exchange", WELCOME_DLX);
		arguments.put("x-dead-letter-routing-key", WELCOME_DLK);
		return new Queue(WELCOME_QUEUE, true, false, false, arguments);
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

