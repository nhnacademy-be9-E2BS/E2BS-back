package com.nhnacademy.back.product.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.tag.domain.entity.ProductTag;

public interface ProductTagJpaRepository extends JpaRepository<ProductTag, Long> {
}
