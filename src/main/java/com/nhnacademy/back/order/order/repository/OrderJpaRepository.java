package com.nhnacademy.back.order.order.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.back.order.order.domain.entity.Order;

public interface OrderJpaRepository extends JpaRepository<Order, String> {
	Page<Order> findAllByOrderByOrderCreatedAtDesc(Pageable pageable);

	Page<Order> findAllByOrderState_OrderStateIdOrderByOrderCreatedAtDesc(Pageable pageable, Long stateId);

	Page<Order> findAllByCustomer_CustomerIdOrderByOrderCreatedAtDesc(Pageable pageable, Long customerId);

	@Query("SELECT COUNT(o) FROM Order o WHERE o.orderState.orderStateName = 'WAIT' AND o.orderState.orderStateName = 'DELIVERY'")
	long countAllOrders();

	List<Order> findByOrderPaymentStatusIsFalseAndOrderCreatedAtBefore(LocalDateTime cutoff);

	List<Order> findAllByOrderState_OrderStateIdAndOrderShipmentDateBefore(long stateId, LocalDate cutoff);

}
