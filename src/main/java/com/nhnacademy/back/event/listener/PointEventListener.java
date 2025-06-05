package com.nhnacademy.back.event.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.event.event.OrderCancelPointEvent;
import com.nhnacademy.back.event.event.OrderCancelPointPaymentEvent;
import com.nhnacademy.back.event.event.OrderPointEvent;
import com.nhnacademy.back.event.event.OrderPointPaymentEvent;
import com.nhnacademy.back.event.event.RegisterPointEvent;
import com.nhnacademy.back.event.event.ReviewImgPointEvent;
import com.nhnacademy.back.event.event.ReviewPointEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

	private final PointHistoryService pointHistoryService;

	// phase = TransactionPhase.AFTER_COMMIT : 이벤트를 발행한 상위 서비스 트랜잭션이 commit 되어야만 실행

	/**
	 * 회원가입 포인트 적립
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRegisterPointEvent(RegisterPointEvent event) {
		log.info("[이벤트 수신] RegisterPointEvent memberId={}", event.getMemberId());
		try {
			String memberId = event.getMemberId();
			pointHistoryService.earnRegisterPoint(memberId);
		} catch (Exception e) {
			log.error("회원가입 포인트 적립 실패", e);
			// 실패한 경우 알림 보내기? 여기 추가
		}
	}

	/**
	 * 이미지 리뷰 포인트 적립
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleReviewImgPointEvent(ReviewImgPointEvent event) {
		try {
			String memberId = event.getMemberId();
			pointHistoryService.earnImgReviewPoint(memberId);
		} catch (Exception e) {
			log.error("이미리 리뷰 포인트 적립 실패", e);
		}
	}

	/**
	 * 일반 리뷰 포인트 적립
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleReviewPointEvent(ReviewPointEvent event) {
		try {
			String memberId = event.getMemberId();
			pointHistoryService.earnReviewPoint(memberId);
		} catch (Exception e) {
			log.error("일반 리뷰 포인트 적립 실패", e);
		}
	}

	/**
	 * 도서 주문 포인트 적립
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleOrderPointEvent(OrderPointEvent event) {
		try {
			Long customerId = event.getCustomerId();
			Long pointFigure = event.getPointFigure();
			pointHistoryService.earnOrderPoint(customerId, pointFigure);
		} catch (Exception e) {
			log.error("도서 주문 포인트 적립 실패", e);
		}
	}

	/**
	 * 주문시 포인트 사용
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleOrderPointPaymentEvent(OrderPointPaymentEvent event) {
		// 포인트 사용한 경우에만 히스토리 추가
		if(event.getPointFigure() > 0) {
			try {
				Long customerId = event.getCustomerId();
				Long pointFigure = event.getPointFigure() * -1;

				pointHistoryService.payPoint(customerId, pointFigure);
			} catch (Exception e) {
				log.error("주문 시 포인트 사용 실패", e);
			}
		}
	}

	/**
	 * 주문취소 시 포인트 복구
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleOrderCancelPointPaymentEvent(OrderCancelPointPaymentEvent event) {
		// 포인트 사용한 경우에만 히스토리 추가
		if(event.getPointFigure() > 0) {
			try {
				Long customerId = event.getCustomerId();
				Long pointFigure = event.getPointFigure();
				pointHistoryService.recoverPoint(customerId, pointFigure);
			} catch (Exception e) {
				log.error("주문취소 포인트 복구 실패", e);
			}
		}
	}

	/**
	 * 주문취소 시 포인트 회수
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleOrderCancelPointEvent(OrderCancelPointEvent event) {
		try {
			Long customerId = event.getCustomerId();
			Long pointFigure = event.getPointFigure() * -1;
			pointHistoryService.retrievePoint(customerId, pointFigure);
		} catch (Exception e) {
			log.error("주문취소 포인트 회수 실패", e);
		}
	}
}
