package com.nhnacademy.back.pointpolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicy;

public interface PointPolicyJpaRepository extends JpaRepository<PointPolicy, Long> {
}
