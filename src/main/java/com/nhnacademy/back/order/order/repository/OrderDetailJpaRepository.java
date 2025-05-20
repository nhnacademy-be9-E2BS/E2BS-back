package com.nhnacademy.back.order.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.order.domain.entity.OrderDetail;

public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail, Long> {
	void deleteByOrderOrderCode(String orderCode);
	List<OrderDetail> findByOrderOrderCode(String orderCode);
}
