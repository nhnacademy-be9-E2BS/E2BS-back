package com.nhnacademy.back.product.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.category.domain.dto.ProductCategoryFlatDTO;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;

public interface ProductCategoryJpaRepository extends JpaRepository<ProductCategory, Long> {
	List<ProductCategory> findByProduct_ProductId(long productProductId);

	@Query("SELECT ProductCategoryFlatDTO(pc.product.productId, pc.category.categoryId) " +
		"FROM ProductCategory pc WHERE pc.product.productId IN :productIds")
	List<ProductCategoryFlatDTO> findFlatCategoryData(@Param("productIds") List<Long> productIds);

	@Modifying
	@Transactional
	@Query("DELETE FROM ProductCategory pc WHERE pc.product.productId = :productId")
	void deleteAllByProductId(@Param("productId") Long productId);

}
