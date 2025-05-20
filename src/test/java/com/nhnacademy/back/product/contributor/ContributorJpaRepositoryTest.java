package com.nhnacademy.back.product.contributor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.product.contributor.domain.entity.Contributor;
import com.nhnacademy.back.product.contributor.domain.entity.Position;
import com.nhnacademy.back.product.contributor.repository.ContributorJpaRepository;
import com.nhnacademy.back.product.contributor.repository.PositionJpaRepository;

@DataJpaTest
@ActiveProfiles("test")
class ContributorJpaRepositoryTest {

	@Autowired
	private ContributorJpaRepository contributorJpaRepository;

	@Autowired
	private PositionJpaRepository positionJpaRepository;

	@Test
	void testSaveContributor() {
		Position position = new Position("작가");
		positionJpaRepository.save(position);
		Contributor contributor = new Contributor("이름", position);
		Contributor existingContributor = contributorJpaRepository.save(contributor);

		assertEquals(contributor, existingContributor);
		assertEquals("이름", existingContributor.getContributorName());
		assertEquals(position, existingContributor.getPosition());
		assertEquals("작가", existingContributor.getPosition().getPositionName());
	}

	@Test
	void testFindContributorByName() {
		Position position = new Position("작가");
		positionJpaRepository.save(position);
		Contributor contributor = new Contributor("이름", position);
		contributorJpaRepository.save(contributor);
		assertTrue(contributorJpaRepository.existsByContributorName(contributor.getContributorName()));
	}
}
