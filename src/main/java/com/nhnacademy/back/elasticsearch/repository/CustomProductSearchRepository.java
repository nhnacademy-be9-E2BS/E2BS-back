package com.nhnacademy.back.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.elasticsearch.domain.document.ProductSortType;

public interface CustomProductSearchRepository {
	List<Long> searchAndSortProductIds(Pageable pageable, String keyword, ProductSortType sortType);

	List<Long> categoryAndSortProductIds(Pageable pageable, Long categoryId, ProductSortType sortType);
}
