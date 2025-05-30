package com.nhnacademy.back.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.event.event.ReviewPointEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewPointEventListener {

	private final PointHistoryService pointHistoryService;

	@Async
	@EventListener
	public void handleReviewPointEvent(ReviewPointEvent event) {
		String memberId = event.getMemberId();
		pointHistoryService.earnReviewPoint(memberId);
	}
}
