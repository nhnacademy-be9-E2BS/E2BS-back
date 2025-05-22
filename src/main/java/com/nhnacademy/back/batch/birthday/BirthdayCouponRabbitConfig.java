package com.nhnacademy.back.batch.birthday;

import java.util.HashMap;
import java.util.Map;

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

	/**
	 * Dead Letter Queue: 실패한 메시지를 담는 큐에 대한 설정
	 */
	public static final String BIRTHDAY_DLX = "E2BS.exchange.dlx";
	public static final String BIRTHDAY_DLQ = "E2BS.birthday.coupon.queue.dlq";
	public static final String BIRTHDAY_DLK = "E2BS.birthday.coupon.key.dlk";

	@Bean
	public Queue birthdayDLQ() {
		return new Queue(BIRTHDAY_DLQ, true);
	}

	@Bean
	public DirectExchange birthdayDLX() {
		return new DirectExchange(BIRTHDAY_DLX);
	}

	@Bean
	public Binding birthdayDLKBinding() {
		return BindingBuilder
			.bind(birthdayDLQ())
			.to(birthdayDLX())
			.with(BIRTHDAY_DLK);
	}

	@Bean
	public Queue birthdayQueue() {
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-dead-letter-exchange", BIRTHDAY_DLX);
		arguments.put("x-dead-letter-routing-key", BIRTHDAY_DLK);
		return new Queue(BIRTHDAY_QUEUE, true, false, false, arguments);
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

