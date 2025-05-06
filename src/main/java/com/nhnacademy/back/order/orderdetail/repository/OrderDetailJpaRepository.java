package com.nhnacademy.back.order.orderdetail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.orderdetail.domain.entity.OrderDetail;

public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail,Long> {
}
