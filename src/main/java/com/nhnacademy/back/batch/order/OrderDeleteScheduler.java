package com.nhnacademy.back.batch.order;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.order.order.domain.entity.Order;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDeleteScheduler {
	private final OrderJpaRepository orderJpaRepository;
	private final OrderDetailJpaRepository orderDetailJpaRepository;

	@Scheduled(fixedRate = 600_000) // 10분
	@Transactional
	public void deleteUnpaidOrders() {
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10);

		List<Order> ordersToDelete = orderJpaRepository.findByOrderPaymentStatusIsFalseAndOrderCreatedAtBefore(cutoff);

		for (Order order : ordersToDelete) {
			orderDetailJpaRepository.deleteByOrderOrderCode(order.getOrderCode());
		}

		int deletedCount = orderJpaRepository.deleteByOrderPaymentStatusIsFalseAndOrderCreatedAtBefore(cutoff);
		log.info("미결제 10분 초과 주문 삭제 : {}건", deletedCount);
	}
}
