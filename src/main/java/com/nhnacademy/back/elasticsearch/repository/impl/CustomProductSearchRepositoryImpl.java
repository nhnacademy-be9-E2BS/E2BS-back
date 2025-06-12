package com.nhnacademy.back.elasticsearch.repository.impl;

import java.time.LocalDate;
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

	private static final String productReviewCount = "productReviewCount";
	private static final String productHits = "productHits";
	private static final String productSearches = "productSearches";
	private static final String productPublishedAt = "productPublishedAt";

	/**
	 * 검색어로 검색 후 정렬
	 */
	@Override
	public Page<Long> searchAndSortProductIds(Pageable pageable, String keyword, ProductSortType sortType) {
		Criteria criteria = new Criteria()
			.or(new Criteria("productTitle").matches(keyword).boost(10.0f))
			.or(new Criteria("productDescription").matches(keyword).boost(3.0f))
			.or(new Criteria("productPublisherName").matches(keyword).boost(2.0f))
			.or(new Criteria("productContributors").matches(keyword).boost(4.0f))
			.or(new Criteria("productTags").matches(keyword).boost(5.0f));

		if (sortType == ProductSortType.RATING) {
			criteria = criteria.and(productReviewCount).greaterThanEqual(100);
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
			criteria = criteria.and(productReviewCount).greaterThanEqual(100);
		}

		return executeSearchWithCriteria(criteria, pageable, sortType);
	}

	/**
	 * 메인페이지 - 인기도서 12권
	 */
	@Override
	public List<Long> bestSellerProductIdsInMain() {
		Criteria criteria = new Criteria();

		Query query = new CriteriaQuery(criteria);
		query.addSort(Sort.by(
			Sort.Order.desc(productHits),
			Sort.Order.desc(productSearches)
		));
		query.setPageable(PageRequest.of(0, 12));

		SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);

		return searchHits.getSearchHits().stream()
			.map(hit -> hit.getContent().getProductId())
			.toList();
	}

	/**
	 * 메인페이지 - 신상 12권
	 */
	@Override
	public List<Long> newestSellerProductIdsInMain() {
		Criteria criteria = new Criteria();

		Query query = new CriteriaQuery(criteria);
		query.addSort(Sort.by(Sort.Order.desc(productPublishedAt)));
		query.setPageable(PageRequest.of(0, 12));

		SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);

		return searchHits.getSearchHits().stream()
			.map(hit -> hit.getContent().getProductId())
			.toList();
	}

	/**
	 * 상품 리스트 - 인기도서 (top 30)
	 */
	@Override
	public Page<Long> bestSellerProductIds(Pageable pageable) {
		if (pageable.getPageNumber() > 2) {
			throw new IllegalArgumentException();
		}

		Criteria criteria = new Criteria();

		Query query = new CriteriaQuery(criteria);
		query.addSort(Sort.by(
			Sort.Order.desc(productHits),
			Sort.Order.desc(productSearches)
		));
		query.setPageable(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

		SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);

		List<Long> productIds = searchHits.getSearchHits().stream()
			.map(hit -> hit.getContent().getProductId())
			.toList();

		return new PageImpl<>(productIds, pageable, searchHits.getTotalHits());
	}

	/**
	 * 상품 리스트 - 신상 (3개월 이내 출판)
	 */
	@Override
	public Page<Long> newestSellerProductIds(Pageable pageable) {
		LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
		Criteria criteria = new Criteria(productPublishedAt).greaterThanEqual(threeMonthsAgo);

		Query query = new CriteriaQuery(criteria);
		query.addSort(Sort.by(Sort.Order.desc(productPublishedAt)));
		query.setPageable(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

		SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class);

		List<Long> productIds = searchHits.getSearchHits().stream()
			.map(hit -> hit.getContent().getProductId())
			.toList();

		return new PageImpl<>(productIds, pageable, searchHits.getTotalHits());
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
				Sort.Order.desc(productHits),
				Sort.Order.desc(productSearches)
			);
			case LATEST -> Sort.by(Sort.Order.desc(productPublishedAt));
			case LOW_PRICE -> Sort.by(Sort.Order.asc("productSalePrice"));
			case HIGH_PRICE -> Sort.by(Sort.Order.desc("productSalePrice"));
			case RATING -> Sort.by(Sort.Order.desc("productReviewRate"));
			case REVIEW_COUNT -> Sort.by(Sort.Order.desc(productReviewCount));
			default -> Sort.unsorted();
		};
	}
}
