package com.nhnacademy.back.product.publisher;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;

@DataJpaTest
@ActiveProfiles("test")
class PublisherJpaRepositoryTest {
	@Autowired
	private PublisherJpaRepository publisherJpaRepository;

	@Test
	@DisplayName("existsByPublisherName 메소드 테스트")
	public void exists_by_publisher_name_test() {
		// given
		publisherJpaRepository.save(new Publisher("new Publisher A"));

		// when & then
		assertTrue(publisherJpaRepository.existsByPublisherName("new Publisher A"));
	}
}
