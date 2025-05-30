package com.nhnacademy.back.pointpolicy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicy;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicyType;

public interface PointPolicyJpaRepository extends JpaRepository<PointPolicy, Long> {
	boolean existsByPointPolicyName(String pointPolicyName);

	List<PointPolicy> findByPointPolicyTypeOrderByPointPolicyIsActiveDescPointPolicyCreatedAtDesc(PointPolicyType pointPolicyType);

	PointPolicy findByPointPolicyTypeAndPointPolicyIsActive(PointPolicyType pointPolicyType, Boolean pointPolicyIsActive);

}
