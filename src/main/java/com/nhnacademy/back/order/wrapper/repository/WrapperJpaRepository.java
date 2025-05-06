package com.nhnacademy.back.order.wrapper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;

public interface WrapperJpaRepository extends JpaRepository<Wrapper,Long> {
}
