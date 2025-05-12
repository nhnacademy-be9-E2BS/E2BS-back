package com.nhnacademy.back.product.tag;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.product.tag.repository.TagJpaRepository;

import lombok.RequiredArgsConstructor;

@DataJpaTest
@ActiveProfiles("test")
@RequiredArgsConstructor
public class TagJpaRepositoryTest {
	private final TagJpaRepository tagJpaRepository;
}
