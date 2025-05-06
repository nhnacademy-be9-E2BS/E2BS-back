package com.nhnacademy.back.order.orderstate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;

public interface OrderStateJpaRepository extends JpaRepository<OrderState,Long> {
}
