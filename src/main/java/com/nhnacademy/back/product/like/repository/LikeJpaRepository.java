package com.nhnacademy.back.product.like.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.back.product.like.domain.dto.response.ResponseLikedProductDTO;
import com.nhnacademy.back.product.like.domain.entity.Like;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {

	/// 회원이 해당 상품을 이미 좋아요 했는지 검증 메소드
	boolean existsByProduct_ProductIdAndCustomer_CustomerId(long productProductId, long customerCustomerId);

	/// 회원 ID와 상품 ID에 해당하는 좋아요 단일 조회 메소드
	Optional<Like> findByCustomer_CustomerIdAndProduct_ProductId(long customerId, long productProductId);

	/// 회원이 좋아요한 상품 페이징 목록 조회 메소드
	@Query("""
    select new com.nhnacademy.back.product.like.domain.dto.response.ResponseLikedProductDTO(
        p.productId,
        p.productTitle,
        p.productSalePrice,
        pub.publisherName,
        l.likeCreatedAt,
        (select count(lk) from Like lk where lk.product.productId = p.productId),
        coalesce(round((select avg(rv.reviewGrade) from Review rv where rv.product.productId = p.productId), 1), 0.0),
        (select count(rv) from Review rv where rv.product.productId = p.productId)
    )
    from Like l
    join l.product p
    join p.publisher pub
    where l.customer.customerId = :customerId
    """)
	Page<ResponseLikedProductDTO> findLikedProductsByCustomerId(Long customerId, Pageable pageable);

	/// 상품 ID로 좋아요 전체 개수 조회 메소드
	long countAllByProduct_ProductId(long productProductId);

}
