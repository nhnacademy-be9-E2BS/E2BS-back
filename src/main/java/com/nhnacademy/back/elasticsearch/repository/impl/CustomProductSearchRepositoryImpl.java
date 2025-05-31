package com.nhnacademy.back.elasticsearch.repository.impl;

import java.util.List;

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

	@Override
	public List<Long> searchAndSortProductIds(Pageable pageable, String keyword, ProductSortType sortType) {
		// 가중치를 곱한 후 점수 합산
		Criteria criteria = new Criteria()
			.or(new Criteria("productTitle").matches(keyword).boost(10.0f))
			.or(new Criteria("productContent").matches(keyword).boost(2.0f))
			.or(new Criteria("productContributors").matches(keyword).boost(3.0f))
			.or(new Criteria("productTags").matches(keyword).boost(5.0f));

		if (sortType == ProductSortType.RATING) {
			criteria = criteria.and("productReviewCount").greaterThanEqual(100);
		}

		Query query = new CriteriaQuery(criteria);

		if (sortType != null && sortType != ProductSortType.NO_SORT) {
			Sort sort = switch (sortType) {
				case POPULARITY -> Sort.by(
					Sort.Order.desc("productSearches"),
					Sort.Order.desc("productHits")
				);
				case LATEST -> Sort.by(Sort.Order.desc("productPublishedAt"));
				case LOW_PRICE -> Sort.by(Sort.Order.asc("productSalePrice"));
				case HIGH_PRICE -> Sort.by(Sort.Order.desc("productSalePrice"));
				case RATING -> Sort.by(Sort.Order.desc("productReviewRate"));
				case REVIEW_COUNT -> Sort.by(Sort.Order.desc("productReviewCount"));
				default -> Sort.unsorted();
			};
			query.addSort(sort);
		}

		query.setPageable(pageable);
		SearchHits<ProductDocument> search = elasticsearchOperations.search(query, ProductDocument.class);

		return search.stream()
			.map(hit -> hit.getContent().getProductId())
			.toList();
	}

	@Override
	public List<Long> categoryAndSortProductIds(Pageable pageable, Long categoryId, ProductSortType sortType) {
		Criteria criteria = Criteria.where("productCategoryIds").in(categoryId);

		if (sortType == ProductSortType.RATING) {
			criteria = criteria.and("productReviewCount").greaterThanEqual(100);
		}

		Query query = new CriteriaQuery(criteria);

		if (sortType != null && sortType != ProductSortType.NO_SORT) {
			Sort sort = switch (sortType) {
				case POPULARITY -> Sort.by(
					Sort.Order.desc("productSearches"),
					Sort.Order.desc("productHits")
				);
				case LATEST -> Sort.by(Sort.Order.desc("productPublishedAt"));
				case LOW_PRICE -> Sort.by(Sort.Order.asc("productSalePrice"));
				case HIGH_PRICE -> Sort.by(Sort.Order.desc("productSalePrice"));
				case RATING -> Sort.by(Sort.Order.desc("productReviewRate"));
				case REVIEW_COUNT -> Sort.by(Sort.Order.desc("productReviewCount"));
				default -> Sort.unsorted();
			};
			query.addSort(sort);
		}

		query.setPageable(pageable);
		SearchHits<ProductDocument> search = elasticsearchOperations.search(query, ProductDocument.class);

		return search.stream()
			.map(hit -> hit.getContent().getProductId())
			.toList();
	}
}
