package com.nhnacademy.back.product.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.image.domain.entity.ProductImage;

public interface ProductImageJpaRepository extends JpaRepository<ProductImage, Long> {
	List<ProductImage> findByProduct_ProductId(long productProductId);

	void deleteByProduct_ProductId(long productId);

}
