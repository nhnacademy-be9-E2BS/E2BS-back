package com.nhnacademy.back.order.order.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.order.order.model.entity.Order;
import com.nhnacademy.back.order.orderstate.domain.entity.OrderState;

public interface OrderJpaRepository extends JpaRepository<Order, String> {
	Page<Order> findAllByOrderByOrderCreatedAtDesc(Pageable pageable);

	Page<Order> findAllByOrderStateOrderByOrderCreatedAtDesc(Pageable pageable, OrderState orderState);

	Page<Order> findAllByOrderCreatedAtBetweenOrderByOrderCreatedAtDesc(Pageable pageable,
		LocalDateTime startDate, LocalDateTime endDate);

	@Query("SELECT o FROM Order o " +
		"WHERE LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :orderCode, '%')) " +
		"ORDER BY o.orderCreatedAt DESC")
	Page<Order> searchByOrderCodeIgnoreCase(
		@Param("orderCode") String orderCode,
		Pageable pageable
	);

	@Query(value = "SELECT o.* FROM `order` o " +
		"JOIN customer c ON o.customer_id = c.customer_id " +
		"JOIN member m ON m.customer_id = c.customer_id " +
		"WHERE LOWER(m.member_id) LIKE LOWER(CONCAT('%', :memberId, '%')) " +
		"ORDER BY o.order_created_at DESC"
		, nativeQuery = true)
	Page<Order> searchByMemberIdIgnoreCase(
		@Param("memberId") String memberId,
		Pageable pageable
	);

	Page<Order> findAllByCustomer_CustomerIdOrderByOrderCreatedAtDesc(Pageable pageable, Long customerId);

	Page<Order> findAllByCustomer_CustomerIdAndOrderStateOrderByOrderCreatedAtDesc(Pageable pageable, Long customerId,
		OrderState orderState);

	Page<Order> findAllByCustomer_CustomerIdAndOrderCreatedAtBetweenOrderByOrderCreatedAtDesc(Pageable pageable,
		Long customerId,
		LocalDateTime startDate, LocalDateTime endDate);

	@Query("SELECT o FROM Order o " +
		"WHERE o.customer.customerId = :customerId " +
		"AND LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :orderCode, '%')) " +
		"ORDER BY o.orderCreatedAt DESC")
	Page<Order> searchByCustomerIdAndOrderCodeIgnoreCase(
		@Param("customerId") Long customerId,
		@Param("orderCode") String orderCode,
		Pageable pageable
	);

	@Query("SELECT COUNT(o) FROM Order o WHERE o.orderState.orderStateName = 'WAIT' AND o.orderState.orderStateName = 'DELIVERY'")
	long countAllOrders();

	List<Order> findByOrderPaymentStatusIsFalseAndOrderCreatedAtBefore(LocalDateTime cutoff);

	List<Order> findAllByOrderState_OrderStateIdAndOrderShipmentDateBefore(long stateId, LocalDate cutoff);

	@Query("SELECT COALESCE(SUM(o.orderPureAmount), 0) " +
		"FROM Order o " +
		"WHERE o.customer.customerId = :customerId " +
		"AND o.orderState.orderStateName = 'COMPLETE' " +
		"AND o.orderCreatedAt >= :threeMonthsAgo")
	Long sumOrderPureAmount(@Param("customerId") Long customerId,
		@Param("threeMonthsAgo") LocalDateTime threeMonthsAgo);

	@Query("SELECT COUNT(o) FROM Order o WHERE o.orderCreatedAt BETWEEN :start AND :end")
	int countOrdersByLocalDateTime(LocalDateTime start, LocalDateTime end);

	Integer countOrdersByCustomer(Customer customer);

	@Query("SELECT o FROM Order o WHERE o.customer = :customer AND o.orderState IN :orderStates")
	List<Order> findOrdersByCustomerAndOrderState(Customer customer, List<OrderState> orderStates);

}
