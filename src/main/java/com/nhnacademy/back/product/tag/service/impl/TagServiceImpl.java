package com.nhnacademy.back.product.tag.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.tag.domain.dto.request.RequestTagDTO;
import com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO;
import com.nhnacademy.back.product.tag.domain.entity.Tag;
import com.nhnacademy.back.product.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.back.product.tag.exception.TagNotFoundException;
import com.nhnacademy.back.product.tag.repository.TagJpaRepository;
import com.nhnacademy.back.product.tag.service.TagService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
	private final TagJpaRepository tagJpaRepository;

	/**
	 *  Tag를 DB에 저장
	 *  Tag가 이미 존재하면 Exception 발생
	 */
	@Override
	public void createTag(RequestTagDTO request) {
		String tagName = request.getTagName();
		if (tagJpaRepository.existsByTagName(tagName)) {
			throw new TagAlreadyExistsException("Tag " + tagName + " already exists");
		}

		Tag tag = new Tag(tagName);
		tagJpaRepository.save(tag);
	}

	/**
	 * DB에 저장된 Tag 목록 전체 조회
	 *
	 */
	@Override
	public Page<ResponseTagDTO> getTags(Pageable pageable) {
		return tagJpaRepository.findAll(pageable)
			.map(tag -> new ResponseTagDTO(
				tag.getTagId(),
				tag.getTagName()
			));
	}

	/**
	 * tag 이름 업데이트
	 */

	@Override
	public void updateTag(long tagId, RequestTagDTO request) {
		if (Objects.isNull(request)) {
			throw new IllegalArgumentException("Request cannot be null");
		}
		if (tagJpaRepository.findById(tagId).isEmpty()) {
			throw new TagNotFoundException("Tag Not Found, id: %d".formatted(tagId));
		}

		if (tagJpaRepository.existsByTagName(request.getTagName())) {
			throw new TagAlreadyExistsException("Tag Already Exists: %s".formatted(request.getTagName()));
		}
		tagJpaRepository.updateByTagId(tagId, request);
	}
}
