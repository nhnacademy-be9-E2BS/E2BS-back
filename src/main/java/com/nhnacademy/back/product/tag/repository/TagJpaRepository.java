package com.nhnacademy.back.product.tag.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.tag.domain.entity.Tag;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {
	boolean existsByTagName(String tagName);

	void deleteTagByTagId(long tagId);

	Optional<Tag> findByTagName(String tagName);
}
