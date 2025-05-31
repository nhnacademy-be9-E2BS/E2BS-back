package com.nhnacademy.back.product.image.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.back.product.image.domain.dto.response.ResponseProductImageDTO;
import com.nhnacademy.back.product.image.domain.entity.ProductImage;

public interface ProductImageJpaRepository extends JpaRepository<ProductImage, Long> {

	void deleteByProduct_ProductId(long productId);

	@Query("select new com.nhnacademy.back.product.image.domain.dto.response.ResponseProductImageDTO(i.productImageId, i.productImagePath) from ProductImage i where i.product.productId = :productId")
	List<ResponseProductImageDTO> findImageDTOsByProductId(@Param("productId") Long productId);

	@Query("SELECT pi FROM ProductImage pi WHERE pi.product.productId IN :productIds")
	List<ProductImage> findAllByProductIds(@Param("productIds") List<Long> productIds);

	default Map<Long, List<ProductImage>> findAllByProductIdsGrouped(List<Long> productIds) {
		return findAllByProductIds(productIds).stream()
			.collect(Collectors.groupingBy(pi -> pi.getProduct().getProductId()));
	}

	@Query("SELECT pi.productImagePath FROM ProductImage pi WHERE pi.product.productId = :productId")
	List<String> findAllByProduct_ProductId(@Param("productId") long productId);

	@Query("SELECT pi.productImageId FROM ProductImage pi WHERE pi.productImagePath IN :productImage")
	Long findByProductImagePath(@Param("productImage") String productImage);

}
