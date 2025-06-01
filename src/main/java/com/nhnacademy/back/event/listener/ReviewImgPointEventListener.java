package com.nhnacademy.back.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.event.event.ReviewImgPointEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewImgPointEventListener {

	private final PointHistoryService pointHistoryService;

	@Async
	@EventListener
	public void handleReviewImgPointEvent(ReviewImgPointEvent event) {
		String memberId = event.getMemberId();
		pointHistoryService.earnImgReviewPoint(memberId);
	}
}
