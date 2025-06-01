package com.nhnacademy.back.batch.order;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.order.order.domain.entity.Order;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDeleteScheduler {
	private final OrderJpaRepository orderJpaRepository;
	private final OrderService orderService;

	@Scheduled(fixedRate = 600_000) // 10분
	@Transactional
	public void deleteUnpaidOrders() {
		int deletedCount = 0;
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10);
		List<Order> ordersToDelete = orderJpaRepository.findByOrderPaymentStatusIsFalseAndOrderCreatedAtBefore(cutoff);

		for (Order order : ordersToDelete) {
			orderService.deleteOrder(order.getOrderCode());
			++deletedCount;
		}

		log.info("미결제 10분 초과 주문 삭제 : {}건", deletedCount);
	}
}
