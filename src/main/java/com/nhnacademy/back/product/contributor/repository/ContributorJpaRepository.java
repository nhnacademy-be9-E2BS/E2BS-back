package com.nhnacademy.back.product.contributor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
import com.nhnacademy.back.product.contributor.domain.entity.Position;

public interface ContributorJpaRepository extends JpaRepository<Contributor, Long> {
	boolean existsByContributorName(String contributorName);

	List<Contributor> findAllByContributorNameIn(List<String> contributorName);

	Optional<Contributor> findByContributorName(String contributorName);

	boolean existsByContributorNameAndPosition(String contributorName, Position position);
}
