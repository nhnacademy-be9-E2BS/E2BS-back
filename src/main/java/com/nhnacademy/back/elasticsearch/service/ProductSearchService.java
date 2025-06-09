package com.nhnacademy.back.elasticsearch.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.elasticsearch.domain.document.ProductSortType;
import com.nhnacademy.back.elasticsearch.domain.dto.request.RequestProductDocumentDTO;

public interface ProductSearchService {
	// 도서 저장 시
	void createProductDocument(RequestProductDocumentDTO request);

	// 검색어로 검색 & 검색어 검색 후 정렬 시
	Page<Long> getProductIdsBySearch(Pageable pageable, String keyword, ProductSortType sortType);

	// 카테고리 리스트 조회 & 조회 후 정렬 시
	Page<Long> getProductIdsByCategoryId(Pageable pageable, Long categoryId, ProductSortType sortType);

	// 메인 페이지 인기 도서
	List<Long> getBestProductIds();

	// 메인 페이지 신상 도서
	List<Long> getNewProductIds();

	// 인기 도서 카테고리 조회
	Page<Long> getBestProductIdsHeader(Pageable pageable);

	// 신상 도서 카테고리 조회
	Page<Long> getNewProductIdsHeader(Pageable pageable);

	// 도서 수정 시
	void updateProductDocument(RequestProductDocumentDTO request);

	// 도서 판매가 수정 시
	void updateProductSalePrice(Long productId, Long productSalePrice);

	// 상세페이지 조회 시 (+1)
	void updateProductDocumentHits(Long productId);

	// 검색 시 (+1)
	void updateProductDocumentSearches(Long productId);

	// 리뷰 작성 시 (평점 다시 계산, 리뷰수 +1)
	void updateProductDocumentReview(Long productId, Integer reviewRate);

	// 태그 이름 수정 시
	void updateProductDocumentTag(Long productId, String beforeName, String afterName);

	// 출판사 이름 수정 시
	void updateProductDocumentPublisher(Long productId, String newName);
}
