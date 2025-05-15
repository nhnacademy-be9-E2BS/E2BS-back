package com.nhnacademy.back.product.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.product.domain.entity.Product;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
	boolean existsByProductIsbn(String isbn);
}
