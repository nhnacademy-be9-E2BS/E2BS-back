package com.nhnacademy.back.elasticsearch.domain.document;

public enum ProductSortType {
	NO_SORT,         // 정렬 안함
	POPULARITY,      // 조회수
	LATEST,          // 최신순
	LOW_PRICE,       // 최저가순
	HIGH_PRICE,      // 최고가순
	RATING,          // 평점순 (리뷰 수 100 이상)
	REVIEW_COUNT     // 리뷰 많은 순
}
