package com.nhnacademy.back.order.orderreturn.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.order.orderreturn.domain.entity.OrderReturn;

public interface OrderReturnJpaRepository extends JpaRepository<OrderReturn, Long> {
	Page<OrderReturn> findByOrder_CustomerOrderByOrderReturnCreatedAtDesc(Customer customer, Pageable pageable);
	Optional<OrderReturn> findByOrder_OrderCode(String orderCode);
	Page<OrderReturn> findAllByOrderByOrderReturnCreatedAtDesc(Pageable pageable);
}
