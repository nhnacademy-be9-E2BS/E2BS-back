package com.nhnacademy.back.order.orderstate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderStateName;

public interface OrderStateJpaRepository extends JpaRepository<OrderState, Long> {
	Optional<OrderState> findByOrderStateName(OrderStateName orderStateName);
}
