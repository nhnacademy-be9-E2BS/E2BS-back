package com.nhnacademy.back.product.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.product.tag.domain.dto.request.RequestTagDTO;
import com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO;
import com.nhnacademy.back.product.tag.domain.entity.Tag;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {
	boolean existsByTagName(String tagName);
	ResponseTagDTO updateByTagId(long tagId, RequestTagDTO request);
}
