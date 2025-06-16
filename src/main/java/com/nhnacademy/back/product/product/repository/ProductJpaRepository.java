package com.nhnacademy.back.product.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

	boolean existsByProductIsbn(String productIsbn);

	Page<Product> findAllByProductState_ProductStateName(ProductStateName productStateName, Pageable pageable);

	@Query("SELECT p FROM ProductCategory pc JOIN pc.product p WHERE pc.category.categoryId = :categoryId")
	Page<Product> findAllByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

	@Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImage WHERE p.productId = :id")
	Optional<Product> findByIdWithImages(@Param("id") Long id);

	List<Product> findAllByPublisher_PublisherId(long publisherPublisherId);

	@Query("SELECT p FROM Product p " +
		"LEFT JOIN ProductCategory pc ON pc.product = p " +
		"LEFT JOIN ProductTag pt ON pt.product = p " +
		"WHERE p.productId != :bookId " +
		"GROUP BY p " +
		"HAVING COUNT(CASE WHEN pc.category.categoryId IN :categoryIds THEN 1 END) + " +
		"       COUNT(CASE WHEN pt.tag.tagId IN :tagIds THEN 1 END) > 0 " +
		"ORDER BY (COUNT(CASE WHEN pc.category.categoryId IN :categoryIds THEN 1 END) + " +
		"          COUNT(CASE WHEN pt.tag.tagId IN :tagIds THEN 1 END)) DESC")
	List<Product> findByCategoriesInOrTagsIn(@Param("categoryIds") List<Long> categoryIds,
		@Param("tagIds") List<Long> tagIds,
		@Param("bookId") Long bookId);
}
