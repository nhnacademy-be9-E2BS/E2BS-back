package com.nhnacademy.back.product.contributor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.contributor.domain.entity.Contributor;

public interface ContributorJpaRepository extends JpaRepository<Contributor, Long> {
	boolean existsByContributorName(String contributorName);

	List<Contributor> findAllByContributorName(List<String> contributorName);

	Optional<Contributor> findByContributorName(String contributorName);
}
