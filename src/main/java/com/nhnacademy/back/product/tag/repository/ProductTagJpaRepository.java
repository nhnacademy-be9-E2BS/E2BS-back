package com.nhnacademy.back.product.tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO;
import com.nhnacademy.back.product.tag.domain.entity.ProductTag;

public interface ProductTagJpaRepository extends JpaRepository<ProductTag, Long> {
	@Query("select new com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO(pt.tag.tagId, pt.tag.tagName) from ProductTag pt where pt.product.productId = :productId")
	List<ResponseTagDTO> findTagDTOsByProductId(@Param("productId") Long productId);

	void deleteByProduct_ProductId(long productId);

	@Query("SELECT pt FROM ProductTag pt JOIN FETCH pt.tag WHERE pt.product.productId IN :productIds")
	List<ProductTag> findAllWithTagsByProductIds(@Param("productIds") List<Long> productIds);

	List<ProductTag> findAllByTag_TagId(long tagTagId);
}
