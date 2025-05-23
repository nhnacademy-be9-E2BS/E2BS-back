package com.nhnacademy.back.product.contributor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.contributor.domain.entity.ProductContributor;

public interface ProductContributorJpaRepository extends JpaRepository<ProductContributor, Long> {
	List<ProductContributor> findByProduct_ProductId(long productId);

	void deleteByProduct_ProductId(long productId);
}
