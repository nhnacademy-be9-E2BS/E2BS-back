package com.nhnacademy.back.order.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.order.domain.entity.Order;

public interface OrderJpaRepository extends JpaRepository<Order, String> {
	Page<Order> findAllByOrderByOrderCreatedAtDesc(Pageable pageable);

	Page<Order> findAllByOrderState_OrderStateIdOrderByOrderCreatedAtDesc(Pageable pageable, Long stateId);

	Page<Order> findAllByCustomer_CustomerIdOrderByOrderCreatedAtDesc(Pageable pageable, Long customerId);
}
