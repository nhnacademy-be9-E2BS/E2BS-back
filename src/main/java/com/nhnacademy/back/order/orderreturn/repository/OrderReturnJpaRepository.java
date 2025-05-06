package com.nhnacademy.back.order.orderreturn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.orderreturn.domain.entity.OrderReturn;

public interface OrderReturnJpaRepository extends JpaRepository<OrderReturn,Long> {
}
