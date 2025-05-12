package com.nhnacademy.back.product.contributor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.contributor.domain.entity.Contributor;

public interface ContributorJpaRepository extends JpaRepository<Contributor, Long> {
	boolean existsByContributorName(String contributorName);
}
