package com.nhnacademy.back.product.tag.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nhnacademy.back.product.publisher.domain.dto.request.RequestPublisherDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.tag.domain.dto.request.RequestTagDTO;
import com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO;
import com.nhnacademy.back.product.tag.domain.entity.Tag;

public interface TagService {
	void createTag(RequestTagDTO request);

	List<Tag> getTags();

	ResponseTagDTO updateTag(long tagId, RequestTagDTO request);
}
