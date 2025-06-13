package com.nhnacademy.back.elasticsearch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import com.nhnacademy.back.elasticsearch.domain.document.ProductDocument;
import com.nhnacademy.back.elasticsearch.domain.document.ProductSortType;
import com.nhnacademy.back.elasticsearch.repository.impl.CustomProductSearchRepositoryImpl;

@MockitoSettings(strictness = Strictness.LENIENT)
class CustomProductSearchRepositoryTest {

	@Mock
	ElasticsearchOperations elasticsearchOperations;

	@InjectMocks
	CustomProductSearchRepositoryImpl repository;

	private ProductDocument createDoc(Long id) {
		return new ProductDocument(id);
	}

	@Test
	@DisplayName("searchAndSortProductIds 메소드 테스트 - sorting")
	void testSearchAndSortProductIds_ratingSort() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		String keyword = "test";
		ProductSortType sortType = ProductSortType.RATING;

		SearchHits<ProductDocument> searchHits = mockSearchHits(List.of(1L, 2L, 3L));
		when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(ProductDocument.class))).thenReturn(
			searchHits);

		// when
		Page<Long> result = repository.searchAndSortProductIds(pageable, keyword, sortType);

		// then
		assertEquals(3, result.getContent().size());
		verify(elasticsearchOperations).search(any(CriteriaQuery.class), eq(ProductDocument.class));
	}

	@Test
	@DisplayName("searchAndSortProductIds 메소드 테스트 - no sorting")
	void testSearchAndSortProductIds_noSort() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		String keyword = "test";
		ProductSortType sortType = ProductSortType.NO_SORT;

		SearchHits<ProductDocument> searchHits = mockSearchHits(List.of(1L, 2L, 3L));

		when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(ProductDocument.class))).thenReturn(
			searchHits);

		// when
		Page<Long> result = repository.searchAndSortProductIds(pageable, keyword, sortType);

		// then
		assertEquals(3, result.getContent().size());
		verify(elasticsearchOperations).search(any(CriteriaQuery.class), eq(ProductDocument.class));
	}

	@Test
	@DisplayName("categoryAndSortProductIds 메소드 테스트 - sorting")
	void testCategoryAndSortProductIds_ratingSort() {
		Pageable pageable = PageRequest.of(0, 5);
		Long categoryId = 100L;
		ProductSortType sortType = ProductSortType.RATING;

		SearchHits<ProductDocument> searchHits = mockSearchHits(List.of(10L, 20L));

		when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(ProductDocument.class))).thenReturn(
			searchHits);

		Page<Long> result = repository.categoryAndSortProductIds(pageable, categoryId, sortType);

		assertEquals(2, result.getContent().size());
		verify(elasticsearchOperations).search(any(CriteriaQuery.class), eq(ProductDocument.class));
	}

	@Test
	@DisplayName("categoryAndSortProductIds 메소드 테스트 - no sorting")
	void testCategoryAndSortProductIds_noSort() {
		Pageable pageable = PageRequest.of(0, 5);
		Long categoryId = 100L;
		ProductSortType sortType = ProductSortType.NO_SORT;

		SearchHits<ProductDocument> searchHits = mockSearchHits(List.of(10L, 20L));

		when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(ProductDocument.class))).thenReturn(
			searchHits);

		Page<Long> result = repository.categoryAndSortProductIds(pageable, categoryId, sortType);

		assertEquals(2, result.getContent().size());
		verify(elasticsearchOperations).search(any(CriteriaQuery.class), eq(ProductDocument.class));
	}

	@Test
	@DisplayName("bestSellerProductIdsInMain 메소드 테스트")
	void testBestSellerProductIdsInMain() {
		SearchHits<ProductDocument> searchHits = mockSearchHits(List.of(11L, 12L, 13L));

		when(elasticsearchOperations.search(any(Query.class), eq(ProductDocument.class))).thenReturn(searchHits);

		List<Long> result = repository.bestSellerProductIdsInMain();

		assertEquals(3, result.size());
		verify(elasticsearchOperations).search(any(Query.class), eq(ProductDocument.class));
	}

	@Test
	@DisplayName("newestSellerProductIdsInMain 메소드 테스트")
	void testNewestSellerProductIdsInMain() {
		SearchHits<ProductDocument> searchHits = mockSearchHits(List.of(21L, 22L));

		when(elasticsearchOperations.search(any(Query.class), eq(ProductDocument.class))).thenReturn(searchHits);

		List<Long> result = repository.newestSellerProductIdsInMain();

		assertEquals(2, result.size());
		verify(elasticsearchOperations).search(any(Query.class), eq(ProductDocument.class));
	}

	@Test
	@DisplayName("bestSellerProductIds 메소드 테스트 - success")
	void testBestSellerProductIds_success() {
		Pageable pageable = PageRequest.of(1, 10);
		SearchHits<ProductDocument> searchHits = mockSearchHits(List.of(31L, 32L, 33L));

		when(elasticsearchOperations.search(any(Query.class), eq(ProductDocument.class))).thenReturn(searchHits);

		Page<Long> result = repository.bestSellerProductIds(pageable);

		assertEquals(3, result.getContent().size());
		verify(elasticsearchOperations).search(any(Query.class), eq(ProductDocument.class));
	}

	@Test
	@DisplayName("bestSellerProductIds 메소드 테스트 - fail")
	void testBestSellerProductIds_pageNumberOverLimit_throws() {
		Pageable pageable = PageRequest.of(3, 10); // page number > 2

		assertThrows(IllegalArgumentException.class, () -> repository.bestSellerProductIds(pageable));
	}

	@Test
	@DisplayName("newestSellerProductIds 메소드 테스트")
	void testNewestSellerProductIds() {
		Pageable pageable = PageRequest.of(0, 10);
		SearchHits<ProductDocument> searchHits = mockSearchHits(List.of(41L, 42L));

		when(elasticsearchOperations.search(any(Query.class), eq(ProductDocument.class))).thenReturn(searchHits);

		Page<Long> result = repository.newestSellerProductIds(pageable);

		assertEquals(2, result.getContent().size());
		verify(elasticsearchOperations).search(any(Query.class), eq(ProductDocument.class));
	}

	private SearchHits<ProductDocument> mockSearchHits(List<Long> ids) {
		SearchHits<ProductDocument> searchHits = mock(SearchHits.class);

		when(searchHits.getTotalHits()).thenReturn((long)ids.size());
		when(searchHits.getMaxScore()).thenReturn(1.0f);

		List<SearchHit<ProductDocument>> hits = ids.stream().map(id -> {
			SearchHit<ProductDocument> hit = mock(SearchHit.class);
			lenient().when(hit.getContent()).thenReturn(createDoc(id));
			return hit;
		}).toList();

		when(searchHits.getSearchHits()).thenReturn(hits);
		when(searchHits.iterator()).thenReturn(hits.iterator());
		when(searchHits.stream()).thenReturn(hits.stream());

		return searchHits;
	}
}
