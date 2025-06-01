package com.nhnacademy.back.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.event.event.OrderPointEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderPointEventListener {

	private final PointHistoryService pointHistoryService;

	@Async
	@EventListener
	public void handleOrderPointEvent(OrderPointEvent event) {
		String memberId = event.getMemberId();
		pointHistoryService.earnOrderPoint(memberId);
	}
}
