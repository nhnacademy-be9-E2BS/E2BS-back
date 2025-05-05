package com.nhnacademy.back.product.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.like.domain.entity.Like;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {
}
