package com.nhnacademy.back.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.back.review.domain.entity.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
	/**
	 * 고객의 리뷰 내역 페이징 조회 메소드
	 */
	Page<Review> findAllByCustomer_CustomerId(long customerCustomerId, Pageable pageable);

	/**
	 * 상품의 리뷰 내역 페이징 조회 메소드
	 */
	Page<Review> findAllByProduct_ProductId(long productProductId, Pageable pageable);

	/**
	 * 상품의 전체 평점 구하는 메소드
	 * - 상품에 리뷰가 없을 수 있으므로 null 방지를 위해 coalesce() 함수 사용
	 *   ex) coalesce(avg(r.reviewGrade), 0) avg(r.reviewGrade) 가 null 이면 0 지정
	 */
	@Query("select coalesce(avg(r.reviewGrade), 0.0) from Review r " +
		   "where r.product.productId = :productId")
	double totalAvgReviewsByProductId(long productId);

	/**
	 * 상품과 리뷰 등급에 해당하는 리뷰 개수 조회 메소드
	 */
	Integer countAllByProduct_ProductIdAndReviewGrade(long productId, int reviewGrade);

	/**
	 * 상품에 해당하는 리뷰 총 개수 조회 메소드
	 */
	Integer countAllByProduct_ProductId(long productProductId);

	/**
	 * 고객이 주문한 상품에 이미 작성한 리뷰가 있는지 여부 확인 메소드
	 */
	/// 성능과 명확성을 위해 count(od) > 0 보다는 exists 를 사용
	/// exists 는 하나만 찾으면 멈추기 때문에 일반적으로 더 빠름
	/// 주문 상태가 완료 된 경우 쿼리, 이 쿼리로 적용할 시 추가로 서비스단에 OrderDetail 을 가져오는 메소드도 수정해야함
	// @Query("SELECT CASE WHEN COUNT(od) > 0 THEN true ELSE false END " +
	// 	"FROM OrderDetail od " +
	// 	"JOIN od.order o " +
	// 	"WHERE o.orderState.orderStateId = 2 " +
	// 	"AND o.customer.customerId = :customerId " +
	// 	"AND od.product.productId = :productId " +
	// 	"AND od.review IS NULL")
	@Query("select exists (" +
		   "  select 1 from OrderDetail od " +
		   "  join od.order o " +
		   "  where o.customer.customerId = :customerId " +
		   "  and od.product.productId = :productId " +
		   "  and od.review is null" +
		   ")")
	boolean existsReviewedOrderDetailsByCustomerIdAndProductId(long customerId, long productId);

	/**
	 * 주문 코드를 통해 작성한 리뷰가 있는지 여부 확인 메소드
	 */
	@Query("select exists (" +
		   "  select 1 from OrderDetail od " +
		   "  join od.order o " +
		   "  where o.orderCode = :orderCode " +
		   "  and od.review is not null" +
		   ")")
	boolean existsReviewedOrderCode(String orderCode);


	@Query("select r " +
		   "from Review r " +
		   "left join OrderDetail od " +
		   "on r.reviewId = od.review.reviewId " +
		   "where od.orderDetailId = :orderDetailId")
	Optional<Review> findByOrderDetailId(long orderDetailId);

}
