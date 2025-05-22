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
		Long id = member.getCustomerId(); // 수정: 고객테이블 -> 회원테이블에 존재하는 ID 만 MQ 로 전달

		rabbitTemplate.convertAndSend(exchange, routingKey, id);
	}
}
