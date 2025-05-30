package com.nhnacademy.back.elasticsearch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.elasticsearch.domain.document.ProductDocument;

public interface CustomProductSearchRepository {
	Page<ProductDocument> searchAndSortProduct(Pageable pageable, String search, String sort);

	Page<ProductDocument> categoryAndSortProduct(Pageable pageable, Long categoryId, String sort);
}
