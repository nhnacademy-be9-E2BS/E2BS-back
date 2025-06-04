package com.nhnacademy.back.product.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.category.domain.entity.Category;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
	List<Category> findAllByParentIsNull();

	boolean existsByParentIsNullAndCategoryName(String categoryName);

	boolean existsByParentCategoryIdAndCategoryName(Long parentId, String categoryName);


	Category findCategoryByCategoryName(String categoryName);

	Category findCategoryByCategoryId(long categoryId);

	boolean existsCategoryByCategoryName(String categoryName);
}
