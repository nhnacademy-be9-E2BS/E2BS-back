package com.nhnacademy.back.order.order.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

	/// 주문 상태가 완료 된 경우 쿼리, 이 쿼리로 적용할 시 추가로 서비스단에 OrderDetail 을 가져오는 메소드도 수정해야함
	// @Query("SELECT CASE WHEN COUNT(od) > 0 THEN true ELSE false END " +
	// 	"FROM OrderDetail od " +
	// 	"JOIN od.order o " +
	// 	"WHERE o.orderState.orderStateId = 2 " +
	// 	"AND o.customer.customerId = :customerId " +
	// 	"AND od.product.productId = :productId " +
	// 	"AND od.review IS NULL")
	@Query("SELECT CASE WHEN COUNT(od) > 0 THEN true ELSE false END " +
		"FROM OrderDetail od " +
		"JOIN od.order o " +
		"WHERE o.customer.customerId = :customerId " +
		"AND od.product.productId = :productId " +
		"AND od.review IS NULL")
	boolean existsOrderDetailByCustomerIdAndProductId(long customerId, long productId);

	@Query("SELECT od " +
		"FROM OrderDetail od " +
		"JOIN od.order o " +
		"WHERE o.customer.customerId = :customerId " +
		"AND od.product.productId = :productId " +
		"AND od.review IS NULL")
	Optional<OrderDetail> findByCustomerIdAndProductId(Long customerId, Long productId);

}
