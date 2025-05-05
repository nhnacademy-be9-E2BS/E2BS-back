package com.nhnacademy.back.product.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.image.domain.entity.ProductImage;

public interface ProductImageJpaRepository extends JpaRepository<ProductImage, Long> {
}
