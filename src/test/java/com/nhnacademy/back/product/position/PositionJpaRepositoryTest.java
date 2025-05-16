package com.nhnacademy.back.product.position;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;

@DataJpaTest
@ActiveProfiles("test")
class PositionJpaRepositoryTest {
	@Autowired
	private PositionJpaRepository positionJpaRepository;

	@Test
	void existsByPositionName() {
		positionJpaRepository.save(new Position("new"));
		assertTrue(positionJpaRepository.existsByPositionName("new"));
	}
}
