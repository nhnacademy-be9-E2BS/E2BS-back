package com.nhnacademy.back.elasticsearch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.elasticsearch.domain.document.ProductSortType;

public interface CustomProductSearchRepository {
	Page<Long> searchAndSortProductIds(Pageable pageable, String keyword, ProductSortType sortType);

	Page<Long> categoryAndSortProductIds(Pageable pageable, Long categoryId, ProductSortType sortType);
}
