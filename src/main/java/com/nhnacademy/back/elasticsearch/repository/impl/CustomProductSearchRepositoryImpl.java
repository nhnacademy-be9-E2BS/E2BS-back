package com.nhnacademy.back.elasticsearch.repository.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

import com.nhnacademy.back.elasticsearch.domain.document.ProductDocument;
import com.nhnacademy.back.elasticsearch.repository.CustomProductSearchRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomProductSearchRepositoryImpl implements CustomProductSearchRepository {

	private final ElasticsearchOperations elasticsearchOperations;

	@Override
	public Page<ProductDocument> searchAndSortProduct(Pageable pageable, String search, String sort) {
		return null;
	}

	@Override
	public Page<ProductDocument> categoryAndSortProduct(Pageable pageable, Long categoryId, String sort) {
		return null;
	}
}
