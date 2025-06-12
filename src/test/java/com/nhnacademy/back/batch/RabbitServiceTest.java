package com.nhnacademy.back.batch;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.batch.service.RabbitService;

@ExtendWith(MockitoExtension.class)
class RabbitServiceTest {

	@Mock
	private RabbitTemplate rabbitTemplate;

	@Mock
	private MemberService memberService;

	@InjectMocks
	private RabbitService rabbitService;

	@Test
	void testSendToRabbitMQ_success() {
		// Given
		String exchange = "test-exchange";
		String routingKey = "test-routingKey";
		String memberId = "user123";
		Long customerId = 42L;

		Member member = mock(Member.class);
		when(memberService.getMemberByMemberId(memberId)).thenReturn(member);
		when(member.getCustomerId()).thenReturn(customerId);

		// When
		rabbitService.sendToRabbitMQ(exchange, routingKey, memberId);

		// Then
		verify(memberService, times(1)).getMemberByMemberId(memberId);
		verify(rabbitTemplate, times(1)).convertAndSend(exchange, routingKey, customerId);
	}
}
