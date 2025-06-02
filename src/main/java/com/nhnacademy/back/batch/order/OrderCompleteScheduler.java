package com.nhnacademy.back.batch.order;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nhnacademy.back.order.order.domain.entity.Order;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderStateName;
import com.nhnacademy.back.order.orderstate.repository.OrderStateJpaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCompleteScheduler {
	private final OrderJpaRepository orderJpaRepository;
	private final OrderStateJpaRepository orderStateJpaRepository;

	@Scheduled(cron = "0 0 * * * *")
	@Transactional
	public void updateShipmentToCompleted() {
		OrderState delivery = orderStateJpaRepository.findByOrderStateName(OrderStateName.DELIVERY).orElse(null);
		OrderState complete = orderStateJpaRepository.findByOrderStateName(OrderStateName.COMPLETE).orElse(null);

		LocalDate cutoff = LocalDate.now().minusDays(1);
		List<Order> shippingOrders = orderJpaRepository.findAllByOrderState_OrderStateIdAndOrderShipmentDateBefore(
			Objects.requireNonNull(delivery).getOrderStateId(), cutoff
		);

		for (Order order : shippingOrders) {
			order.updateOrderState(complete); // 배송완료 상태로 변경
			log.info("배송완료 : {}", order.getOrderCode());
		}
	}
}
