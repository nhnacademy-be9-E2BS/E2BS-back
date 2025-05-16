package com.nhnacademy.back.product.tag;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.product.tag.domain.entity.Tag;
import com.nhnacademy.back.product.tag.repository.TagJpaRepository;


@DataJpaTest
@ActiveProfiles("test")
public class TagJpaRepositoryTest {
	@Autowired
	private TagJpaRepository tagJpaRepository;

	@Test
	@DisplayName("existsByTagName 메서드 작동 테스트")
	void existsByTagNameTest() {
		//given
		tagJpaRepository.save(new Tag("new Tag"));

		//when & then
		assertTrue(tagJpaRepository.existsByTagName("new Tag"));
	}

}
