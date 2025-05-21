package com.nhnacademy.back.batch.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitService {

	private final RabbitTemplate rabbitTemplate;
	private final MemberService memberService;

	public void sendToRabbitMQ(String exchange, String routingKey, String memberId) {
		Member member = memberService.getMemberByMemberId(memberId);
		Long id = member.getCustomer().getCustomerId();

		rabbitTemplate.convertAndSend(exchange, routingKey, id);
	}
}
