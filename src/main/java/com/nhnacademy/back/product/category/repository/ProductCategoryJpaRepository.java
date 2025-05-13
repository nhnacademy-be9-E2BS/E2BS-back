package com.nhnacademy.back.product.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.category.domain.entity.ProductCategory;

public interface ProductCategoryJpaRepository extends JpaRepository<ProductCategory, Long> {
	List<ProductCategory> findByProduct_ProductId(long productProductId);
}
