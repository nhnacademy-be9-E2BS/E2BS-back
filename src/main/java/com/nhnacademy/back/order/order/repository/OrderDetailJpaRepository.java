package com.nhnacademy.back.order.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.order.domain.entity.OrderDetail;

public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail,Long> {
}
