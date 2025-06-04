package com.nhnacademy.back.elasticsearch.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import com.nhnacademy.back.elasticsearch.domain.document.ProductDocument;
import com.nhnacademy.back.elasticsearch.domain.document.ProductSortType;
import com.nhnacademy.back.elasticsearch.repository.CustomProductSearchRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomProductSearchRepositoryImpl implements CustomProductSearchRepository {

	private final ElasticsearchOperations elasticsearchOperations;

	/**
	 * 검색어로 검색 후 정렬
	 */
	@Override
	public Page<Long> searchAndSortProductIds(Pageable pageable, String keyword, ProductSortType sortType) {
		Criteria criteria = new Criteria()
			.or(new Criteria("productTitle").matches(keyword).boost(10.0f))
			.or(new Criteria("productContent").matches(keyword).boost(3.0f))
			.or(new Criteria("productPublisherName").matches(keyword).boost(2.0f))
			.or(new Criteria("productContributors").matches(keyword).boost(4.0f))
			.or(new Criteria("productTags").matches(keyword).boost(5.0f));

		if (sortType == ProductSortType.RATING) {
			criteria = criteria.and("productReviewCount").greaterThanEqual(100);
		}

		return executeSearchWithCriteria(criteria, pageable, sortType);
	}

	/**
	 * 카테고리로 검색 후 정렬
	 */
	@Override
	public Page<Long> categoryAndSortProductIds(Pageable pageable, Long categoryId, ProductSortType sortType) {
		Criteria criteria = Criteria.where("productCategoryIds").in(categoryId);

		if (sortType == ProductSortType.RATING) {
			criteria = criteria.and("productReviewCount").greaterThanEqual(100);
		}

		return executeSearchWithCriteria(criteria, pageable, sortType);
	}

	/**
	 * 공통 쿼리 실행 메서드
	 */
	private Page<Long> executeSearchWithCriteria(Criteria criteria, Pageable pageable, ProductSortType sortType) {
		Query query = new CriteriaQuery(criteria);

		Sort sort = getSortBySortType(sortType);
		if (sort.isSorted()) {
			query.addSort(sort);
		}

		query.setPageable(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
		SearchHits<ProductDocument> search = elasticsearchOperations.search(query, ProductDocument.class);

		List<Long> idList = search.stream()
			.map(hit -> hit.getContent().getProductId())
			.toList();

		return new PageImpl<>(idList, pageable, search.getTotalHits());
	}

	/**
	 * 정렬 타입에 따른 Sort 생성
	 */
	private Sort getSortBySortType(ProductSortType sortType) {
		if (sortType == null || sortType == ProductSortType.NO_SORT) {
			return Sort.unsorted();
		}

		return switch (sortType) {
			case POPULARITY -> Sort.by(
				Sort.Order.desc("productHits"),
				Sort.Order.desc("productSearches")
			);
			case LATEST -> Sort.by(Sort.Order.desc("productPublishedAt"));
			case LOW_PRICE -> Sort.by(Sort.Order.asc("productSalePrice"));
			case HIGH_PRICE -> Sort.by(Sort.Order.desc("productSalePrice"));
			case RATING -> Sort.by(Sort.Order.desc("productReviewRate"));
			case REVIEW_COUNT -> Sort.by(Sort.Order.desc("productReviewCount"));
			default -> Sort.unsorted();
		};
	}
}
