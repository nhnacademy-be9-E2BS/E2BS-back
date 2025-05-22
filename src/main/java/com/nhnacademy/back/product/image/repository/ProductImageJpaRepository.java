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
	List<ProductImage> findAllByProduct_ProductId(Long productId);

	void deleteByProduct_ProductId(long productId);

	@Query("SELECT ResponseProductImageDTO(pi.product.productId, pi.productImagePath) " +
		"FROM ProductImage pi WHERE pi.product.productId IN :productIds")
	List<ResponseProductImageDTO> findProductImageDTOsByProductIds(@Param("productIds") List<Long> productIds);

	default Map<Long, List<ResponseProductImageDTO>> findGroupedDTOsByProductIds(List<Long> productIds) {
		return findProductImageDTOsByProductIds(productIds).stream()
			.collect(Collectors.groupingBy(ResponseProductImageDTO::getProductId));
	}
}
