package com.nhnacademy.back.product.like.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.back.product.like.domain.entity.Like;
import com.nhnacademy.back.product.product.domain.entity.Product;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {
	boolean existsByProduct_ProductIdAndCustomer_CustomerId(long productProductId, long customerCustomerId);

	Optional<Like> findByProduct_ProductIdAndCustomer_CustomerId(long productProductId, long customerCustomerId);

	@Query("select l.product from Like l "
		+ "where l.customer.customerId = :customerId")
	Page<Product> findLikedProductsByCustomerId(long customerId, Pageable pageable);

	long countAllByProduct_ProductId(long productProductId);

	Optional<Like> findByCustomer_CustomerIdAndProduct_ProductId(long customerId, long productProductId);
}
