package com.nhnacademy.back.event;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.event.event.OrderCancelPointEvent;
import com.nhnacademy.back.event.event.OrderCancelPointPaymentEvent;
import com.nhnacademy.back.event.event.OrderPointEvent;
import com.nhnacademy.back.event.event.OrderPointPaymentEvent;
import com.nhnacademy.back.event.event.RegisterPointEvent;
import com.nhnacademy.back.event.event.ReviewImgPointEvent;
import com.nhnacademy.back.event.event.ReviewPointEvent;
import com.nhnacademy.back.event.listener.PointEventListener;

@ExtendWith(MockitoExtension.class)
class PointEventListenerTest {

	@InjectMocks
	private PointEventListener pointEventListener;

	@Mock
	private PointHistoryService pointHistoryService;

	@Test
	void handleRegisterPointEvent_shouldEarnRegisterPoint() {
		RegisterPointEvent event = new RegisterPointEvent("member123");

		pointEventListener.handleRegisterPointEvent(event);

		verify(pointHistoryService).earnRegisterPoint("member123");
	}

	@Test
	void handleReviewImgPointEvent_shouldEarnImgReviewPoint() {
		ReviewImgPointEvent event = new ReviewImgPointEvent("member123");

		pointEventListener.handleReviewImgPointEvent(event);

		verify(pointHistoryService).earnImgReviewPoint("member123");
	}

	@Test
	void handleReviewPointEvent_shouldEarnReviewPoint() {
		ReviewPointEvent event = new ReviewPointEvent("member123");

		pointEventListener.handleReviewPointEvent(event);

		verify(pointHistoryService).earnReviewPoint("member123");
	}

	@Test
	void handleOrderPointEvent_shouldEarnOrderPoint() {
		OrderPointEvent event = new OrderPointEvent(1L, 500L);

		pointEventListener.handleOrderPointEvent(event);

		verify(pointHistoryService).earnOrderPoint(1L, 500L);
	}

	@Test
	void handleOrderPointPaymentEvent_shouldPayPoint_whenPointUsed() {
		OrderPointPaymentEvent event = new OrderPointPaymentEvent(1L, 200L);

		pointEventListener.handleOrderPointPaymentEvent(event);

		verify(pointHistoryService).payPoint(1L, -200L);
	}

	@Test
	void handleOrderPointPaymentEvent_shouldNotPayPoint_whenNoPointUsed() {
		OrderPointPaymentEvent event = new OrderPointPaymentEvent(1L, 0L);

		pointEventListener.handleOrderPointPaymentEvent(event);

		verify(pointHistoryService, never()).payPoint(anyLong(), anyLong());
	}

	@Test
	void handleOrderCancelPointPaymentEvent_shouldRecoverPoint_whenPointUsed() {
		OrderCancelPointPaymentEvent event = new OrderCancelPointPaymentEvent(1L, 300L);

		pointEventListener.handleOrderCancelPointPaymentEvent(event);

		verify(pointHistoryService).recoverPoint(1L, 300L);
	}

	@Test
	void handleOrderCancelPointPaymentEvent_shouldNotRecoverPoint_whenNoPointUsed() {
		OrderCancelPointPaymentEvent event = new OrderCancelPointPaymentEvent(1L, 0L);

		pointEventListener.handleOrderCancelPointPaymentEvent(event);

		verify(pointHistoryService, never()).recoverPoint(anyLong(), anyLong());
	}

	@Test
	void handleOrderCancelPointEvent_shouldRetrievePoint() {
		OrderCancelPointEvent event = new OrderCancelPointEvent(1L, 150L);

		pointEventListener.handleOrderCancelPointEvent(event);

		verify(pointHistoryService).retrievePoint(1L, -150L);
	}
}
