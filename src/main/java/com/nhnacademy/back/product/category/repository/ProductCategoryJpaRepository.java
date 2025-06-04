package com.nhnacademy.back.product.category.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.category.domain.dto.ProductCategoryFlatDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;

public interface ProductCategoryJpaRepository extends JpaRepository<ProductCategory, Long> {
	List<ProductCategory> findByProduct_ProductId(long productProductId);

	@Query(
		"SELECT new com.nhnacademy.back.product.category.domain.dto.ProductCategoryFlatDTO(pc.product.productId, pc.category.categoryId) "
			+
			"FROM ProductCategory pc WHERE pc.product.productId IN :productIds")
	List<ProductCategoryFlatDTO> findFlatCategoryData(@Param("productIds") List<Long> productIds);

	@Modifying
	@Transactional
	@Query("DELETE FROM ProductCategory pc WHERE pc.product.productId = :productId")
	void deleteAllByProductId(@Param("productId") Long productId);

	@Query("select new com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO(pc.category.categoryId, pc.category.categoryName, null) from ProductCategory pc where pc.product.productId = :productId")
	List<ResponseCategoryDTO> findCategoryDTOsByProductId(@Param("productId") Long productId);

	@Query("SELECT pc FROM ProductCategory pc JOIN FETCH pc.category WHERE pc.product.productId IN :productIds")
	List<ProductCategory> findAllWithCategoriesByProductIds(@Param("productIds") List<Long> productIds);

	default Map<Long, List<Category>> findCategoriesGroupedByProductIds(List<Long> productIds) {
		return findAllWithCategoriesByProductIds(productIds).stream()
			.collect(Collectors.groupingBy(
				pc -> pc.getProduct().getProductId(),
				Collectors.mapping(ProductCategory::getCategory, Collectors.toList())
			));
	}

	List<ProductCategory> findProductIdByCategory(Category category);

	@Query("SELECT pc.category.categoryId FROM ProductCategory pc WHERE pc.product.productId = :productId")
	List<Long> findCategoryIdsByProductId(@Param("productId") Long productId);
}
