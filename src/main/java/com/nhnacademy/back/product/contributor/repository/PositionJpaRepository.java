package com.nhnacademy.back.product.contributor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.contributor.domain.entity.Position;

public interface PositionJpaRepository extends JpaRepository<Position, Long> {
	boolean existsByPositionName(String positionName);

	Optional<Position> findByPositionName(String positionName);
}
