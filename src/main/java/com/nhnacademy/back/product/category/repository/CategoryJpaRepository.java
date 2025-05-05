package com.nhnacademy.back.product.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.category.domain.entity.Category;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
}
