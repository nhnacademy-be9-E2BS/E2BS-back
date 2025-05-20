package com.nhnacademy.back.product.tag.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.product.tag.domain.dto.request.RequestTagDTO;
import com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO;

public interface TagService {
	void createTag(RequestTagDTO request);

	Page<ResponseTagDTO> getTags(Pageable pageable);

	void updateTag(long tagId, RequestTagDTO request);

	void deleteTag(long tagId, RequestTagDTO request);
}
