package com.nhnacademy.back.common.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
		Jackson2JsonMessageConverter messageConverter) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter);
		return template;
	}

	/**
	 * RabbitAdmin 이라는 Spring 컴포넌트가 queue, exchange, binding 을 자동으로 MQ 서버에 등록
	 * Dead Letter Queue: 실패한 메시지를 담는 큐에 대한 설정
	 */
	public static final String DIRECT_EXCHANGE = "E2BS.exchange";
	public static final String DIRECT_QUEUE = "E2BS.direct.coupon.queue";
	public static final String DIRECT_ROUTING_KEY = "E2BS.direct.coupon.key";
	public static final String DIRECT_DLX = "E2BS.exchange.dlx";
	public static final String DIRECT_DLQ = "E2BS.direct.coupon.queue.dlq";
	public static final String DIRECT_DLK = "E2BS.direct.coupon.key.dlk";
	public static final String BIRTHDAY_EXCHANGE = "E2BS.exchange";
	public static final String BIRTHDAY_QUEUE = "E2BS.birthday.coupon.queue";
	public static final String BIRTHDAY_ROUTING_KEY = "E2BS.birthday.coupon.key";
	public static final String BIRTHDAY_DLX = "E2BS.exchange.dlx";
	public static final String BIRTHDAY_DLQ = "E2BS.birthday.coupon.queue.dlq";
	public static final String BIRTHDAY_DLK = "E2BS.birthday.coupon.key.dlk";
	public static final String WELCOME_EXCHANGE = "E2BS.exchange";
	public static final String WELCOME_QUEUE = "E2BS.welcome.coupon.queue";
	public static final String WELCOME_ROUTING_KEY = "E2BS.welcome.coupon.key";
	public static final String WELCOME_DLX = "E2BS.exchange.dlx";
	public static final String WELCOME_DLQ = "E2BS.welcome.coupon.queue.dlq";
	public static final String WELCOME_DLK = "E2BS.welcome.coupon.key.dlk";

	private final String DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
	private final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

	@Bean
	public Queue directDLQ() {
		return new Queue(DIRECT_DLQ, true);
	}

	@Bean
	public DirectExchange directDLX() {
		return new DirectExchange(DIRECT_DLX);
	}

	@Bean
	public Binding directDLKBinding() {
		return BindingBuilder
			.bind(directDLQ())
			.to(directDLX())
			.with(DIRECT_DLK);
	}

	@Bean
	public Queue directQueue() {
		Map<String, Object> arguments = new HashMap<>();
		arguments.put(DEAD_LETTER_EXCHANGE, DIRECT_DLX);
		arguments.put(DEAD_LETTER_ROUTING_KEY, DIRECT_DLK);
		return new Queue(DIRECT_QUEUE, true, false, false, arguments);
	}

	@Bean
	public DirectExchange directExchange() {
		return new DirectExchange(DIRECT_EXCHANGE);
	}

	@Bean
	public Binding directBinding() {
		return BindingBuilder
			.bind(directQueue())
			.to(directExchange())
			.with(DIRECT_ROUTING_KEY);
	}



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
		arguments.put(DEAD_LETTER_EXCHANGE, BIRTHDAY_DLX);
		arguments.put(DEAD_LETTER_ROUTING_KEY, BIRTHDAY_DLK);
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
		arguments.put(DEAD_LETTER_EXCHANGE, WELCOME_DLX);
		arguments.put(DEAD_LETTER_ROUTING_KEY, WELCOME_DLK);
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
