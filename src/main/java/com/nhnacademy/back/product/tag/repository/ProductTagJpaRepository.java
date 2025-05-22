package com.nhnacademy.back.product.tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.tag.domain.entity.ProductTag;

public interface ProductTagJpaRepository extends JpaRepository<ProductTag, Long> {
	List<ProductTag> findByProduct_ProductId(long productId);

	void deleteByProduct_ProductId(long productId);
}
