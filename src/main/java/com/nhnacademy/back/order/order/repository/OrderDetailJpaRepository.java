package com.nhnacademy.back.order.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.back.order.order.model.entity.OrderDetail;

public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail, Long> {
	void deleteByOrderOrderCode(String orderCode);

	List<OrderDetail> findByOrderOrderCode(String orderCode);

	@Query("SELECT SUM(od.orderDetailPerPrice * od.orderQuantity) FROM OrderDetail od")
	Long getTotalSales();

	@Query("SELECT SUM(od.orderDetailPerPrice * od.orderQuantity) FROM OrderDetail od WHERE od.order.orderCreatedAt BETWEEN :start AND :end")
	Long getTotalMonthlySales(LocalDateTime start, LocalDateTime end);

	@Query("SELECT SUM(od.orderDetailPerPrice * od.orderQuantity) FROM OrderDetail od WHERE od.order.orderCreatedAt BETWEEN :start AND :end")
	Long getTotalDailySales(LocalDateTime start, LocalDateTime end);

	@Query("SELECT od " +
		"FROM OrderDetail od " +
		"JOIN od.order o " +
		"WHERE o.customer.customerId = :customerId " +
		"AND od.product.productId = :productId " +
		"AND od.review IS NULL " +
		"ORDER BY o.orderCreatedAt")
	List<OrderDetail> findByCustomerIdAndProductId(Long customerId, Long productId);

}
