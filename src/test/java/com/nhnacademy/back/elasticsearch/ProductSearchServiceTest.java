package com.nhnacademy.back.elasticsearch;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.elasticsearch.domain.document.ProductDocument;
import com.nhnacademy.back.elasticsearch.domain.document.ProductSortType;
import com.nhnacademy.back.elasticsearch.domain.dto.request.RequestProductDocumentDTO;
import com.nhnacademy.back.elasticsearch.repository.CustomProductSearchRepository;
import com.nhnacademy.back.elasticsearch.repository.ProductSearchRepository;
import com.nhnacademy.back.elasticsearch.service.ProductSearchService;
import com.nhnacademy.back.elasticsearch.service.impl.ProductSearchServiceImpl;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTest {
	@InjectMocks
	private ProductSearchServiceImpl productSearchService;
	@Mock
	private ProductSearchRepository productSearchRepository;
	@Mock
	private CustomProductSearchRepository customProductSearchRepository;
	@Mock
	private ProductSearchService self;

	@Test
	@DisplayName("create product document - success")
	void create_product_document_success_test() {
		// given
		RequestProductDocumentDTO request = new RequestProductDocumentDTO(
			1L, "title", "description", "publisher", LocalDate.now(),
			10000L, List.of("contributor"), List.of("tag"), List.of(1L, 2L));
		when(productSearchRepository.existsById(anyLong())).thenReturn(false);

		// when
		productSearchService.createProductDocument(request);

		// then
		verify(productSearchRepository, times(1)).save(any(ProductDocument.class));
	}

	@Test
	@DisplayName("create product document - fail")
	void create_product_document_fail_test() {
		// given
		RequestProductDocumentDTO request = new RequestProductDocumentDTO(
			1L, "title", "description", "publisher", LocalDate.now(),
			10000L, List.of("contributor"), List.of("tag"), List.of(1L, 2L));
		when(productSearchRepository.existsById(anyLong())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> productSearchService.createProductDocument(request))
			.isInstanceOf(ProductAlreadyExistsException.class);
	}

	@Test
	@DisplayName("get productIds by search - success")
	void get_product_ids_by_search_success_test() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		String keyword = "keyword";
		ProductSortType productSortType = ProductSortType.LATEST;

		List<Long> productIdList = List.of(1L);
		Page<Long> mockPage = new PageImpl<>(productIdList, pageable, productIdList.size());

		RequestProductDocumentDTO request = new RequestProductDocumentDTO(
			1L, "title", "description", "publisher", LocalDate.now(),
			10000L, List.of("contributor"), List.of("tag"), List.of(1L, 2L));

		when(customProductSearchRepository.searchAndSortProductIds(pageable, keyword, productSortType)).thenReturn(
			mockPage);

		// when
		Page<Long> result = productSearchService.getProductIdsBySearch(pageable, keyword, productSortType);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0)).isEqualTo(1L);
	}

	@Test
	@DisplayName("get productIds by search - fail")
	void get_product_ids_by_search_fail_test() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		String keyword = "";
		ProductSortType productSortType = ProductSortType.LATEST;

		// when & then
		assertThatThrownBy(() -> productSearchService.getProductIdsBySearch(pageable, keyword, productSortType))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("get productIds by categoryId - success")
	void get_product_ids_by_category_id_success_test() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Long categoryId = 1L;
		ProductSortType productSortType = ProductSortType.LATEST;

		List<Long> productIdList = List.of(1L, 2L, 3L);
		Page<Long> mockPage = new PageImpl<>(productIdList, pageable, productIdList.size());

		when(customProductSearchRepository.categoryAndSortProductIds(pageable, categoryId, productSortType)).thenReturn(
			mockPage);

		// when
		Page<Long> result = productSearchService.getProductIdsByCategoryId(pageable, categoryId, productSortType);

		// then
		assertThat(result.getContent()).hasSize(3);
		assertThat(result.getContent().get(0)).isEqualTo(1L);
		assertThat(result.getContent().get(1)).isEqualTo(2L);
		assertThat(result.getContent().get(2)).isEqualTo(3L);
	}

	@Test
	@DisplayName("get productIds by categoryId - fail")
	void get_product_ids_by_category_id_fail_test() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Long categoryId = -1L;
		ProductSortType productSortType = ProductSortType.LATEST;

		// when & then
		assertThatThrownBy(() -> productSearchService.getProductIdsByCategoryId(pageable, categoryId, productSortType))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("get best product ids")
	void get_best_product_ids_test() {
		// given
		List<Long> productIds = List.of(1L);
		when(customProductSearchRepository.bestSellerProductIdsInMain()).thenReturn(productIds);

		// when
		List<Long> result = productSearchService.getBestProductIds();

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(1L);
	}

	@Test
	@DisplayName("get new product ids")
	void get_new_product_ids_test() {
		// given
		List<Long> productIds = List.of(1L, 2L);
		when(customProductSearchRepository.newestSellerProductIdsInMain()).thenReturn(productIds);

		// when
		List<Long> result = productSearchService.getNewProductIds();

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0)).isEqualTo(1L);
		assertThat(result.get(1)).isEqualTo(2L);
	}

	@Test
	@DisplayName("get best product ids header")
	void get_best_product_ids_header_test() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		List<Long> productIdList = List.of(1L, 2L, 3L);
		Page<Long> mockPage = new PageImpl<>(productIdList, pageable, productIdList.size());

		when(customProductSearchRepository.bestSellerProductIds(pageable)).thenReturn(mockPage);

		// when
		Page<Long> result = productSearchService.getBestProductIdsHeader(pageable);

		// then
		assertThat(result.getContent()).hasSize(3);
		assertThat(result.getContent().get(0)).isEqualTo(1L);
		assertThat(result.getContent().get(1)).isEqualTo(2L);
		assertThat(result.getContent().get(2)).isEqualTo(3L);
	}

	@Test
	@DisplayName("get new product ids header")
	void get_new_product_ids_header_test() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		List<Long> productIdList = List.of(4L, 5L, 6L);
		Page<Long> mockPage = new PageImpl<>(productIdList, pageable, productIdList.size());

		when(customProductSearchRepository.newestSellerProductIds(pageable)).thenReturn(mockPage);

		// when
		Page<Long> result = productSearchService.getNewProductIdsHeader(pageable);

		// then
		assertThat(result.getContent()).hasSize(3);
		assertThat(result.getContent().get(0)).isEqualTo(4L);
		assertThat(result.getContent().get(1)).isEqualTo(5L);
		assertThat(result.getContent().get(2)).isEqualTo(6L);
	}

	@Test
	@DisplayName("update product document - success")
	void update_product_document_success_test() {
		// given
		RequestProductDocumentDTO request = new RequestProductDocumentDTO(
			1L, "title", "description", "publisher", LocalDate.now(),
			10000L, List.of("contributor"), List.of("tag"), List.of(1L, 2L));
		ProductDocument productDocument = new ProductDocument(request);
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.of(productDocument));

		// when
		productSearchService.updateProductDocument(request);

		// then
		verify(productSearchRepository, times(1)).save(any(ProductDocument.class));
	}

	@Test
	@DisplayName("update product document - fail")
	void update_product_document_fail_test() {
		// given
		RequestProductDocumentDTO request = new RequestProductDocumentDTO(
			1L, "title", "description", "publisher", LocalDate.now(),
			10000L, List.of("contributor"), List.of("tag"), List.of(1L, 2L));
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productSearchService.updateProductDocument(request))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("update product sale price - success")
	void update_product_sale_price_success_test() {
		// given
		Long productId = 1L;
		Long productSalePrice = 8000L;
		RequestProductDocumentDTO request = new RequestProductDocumentDTO(
			1L, "title", "description", "publisher", LocalDate.now(),
			10000L, List.of("contributor"), List.of("tag"), List.of(1L, 2L));
		ProductDocument productDocument = new ProductDocument(request);
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.of(productDocument));

		// when
		productSearchService.updateProductSalePrice(productId, productSalePrice);

		// then
		verify(productSearchRepository, times(1)).save(any(ProductDocument.class));
	}

	@Test
	@DisplayName("update product sale price - fail")
	void update_product_sale_price_fail_test() {
		// given
		Long productId = 1L;
		Long productSalePrice = 8000L;
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productSearchService.updateProductSalePrice(productId, productSalePrice))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("update product document hits - success")
	void update_product_document_hits_success_test() {
		Long productId = 1L;
		RequestProductDocumentDTO request = new RequestProductDocumentDTO(
			1L, "title", "description", "publisher", LocalDate.now(),
			10000L, List.of("contributor"), List.of("tag"), List.of(1L, 2L));
		ProductDocument productDocument = new ProductDocument(request);
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.of(productDocument));

		// when
		productSearchService.updateProductDocumentHits(productId);

		// then
		verify(productSearchRepository, times(1)).save(any(ProductDocument.class));
	}

	@Test
	@DisplayName("update product document hits - fail")
	void update_product_document_hits_fail_test() {
		// given
		Long productId = 1L;
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productSearchService.updateProductDocumentHits(productId))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("update product document searches - success")
	void update_product_document_searches_success_test() {
		Long productId = 1L;
		RequestProductDocumentDTO request = new RequestProductDocumentDTO(
			1L, "title", "description", "publisher", LocalDate.now(),
			10000L, List.of("contributor"), List.of("tag"), List.of(1L, 2L));
		ProductDocument productDocument = new ProductDocument(request);
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.of(productDocument));

		// when
		productSearchService.updateProductDocumentSearches(productId);

		// then
		verify(productSearchRepository, times(1)).save(any(ProductDocument.class));
	}

	@Test
	@DisplayName("update product document searches - fail")
	void update_product_document_searches_fail_test() {
		// given
		Long productId = 1L;
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productSearchService.updateProductDocumentSearches(productId))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("update product document review - success")
	void update_product_document_review_success_test() {
		// given
		Long productId = 1L;
		Integer reviewRate = 5;
		RequestProductDocumentDTO request = new RequestProductDocumentDTO(
			1L, "title", "description", "publisher", LocalDate.now(),
			10000L, List.of("contributor"), List.of("tag"), List.of(1L, 2L));
		ProductDocument productDocument = new ProductDocument(request);
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.of(productDocument));

		// when
		productSearchService.updateProductDocumentReview(productId, reviewRate);

		// then
		verify(productSearchRepository, times(1)).save(any(ProductDocument.class));
	}

	@Test
	@DisplayName("update product document review - fail")
	void update_product_document_review_fail_test() {
		// given
		Long productId = 1L;
		Integer reviewRate = 5;
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productSearchService.updateProductDocumentReview(productId, reviewRate))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("update product document tag - success")
	void update_product_document_tag_success_test() {
		// given
		Long productId = 1L;
		String beforeName = "before tag";
		String afterName = "after tag";
		RequestProductDocumentDTO request = new RequestProductDocumentDTO(
			1L, "title", "description", "publisher", LocalDate.now(),
			10000L, List.of("contributor"), new ArrayList<>(List.of(beforeName)), List.of(1L, 2L));
		ProductDocument productDocument = new ProductDocument(request);
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.of(productDocument));

		// when
		productSearchService.updateProductDocumentTag(productId, beforeName, afterName);

		// then
		verify(productSearchRepository, times(1)).save(any(ProductDocument.class));
	}

	@Test
	@DisplayName("update product document tag - fail")
	void update_product_document_tag_fail_test() {
		// given
		Long productId = 1L;
		String beforeName = "before tag";
		String afterName = "after tag";
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productSearchService.updateProductDocumentTag(productId, beforeName, afterName))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("update product document publisher - success")
	void update_product_document_publisher_success_test() {
		// given
		Long productId = 1L;
		String newName = "new publisher";
		RequestProductDocumentDTO request = new RequestProductDocumentDTO(
			1L, "title", "description", "publisher", LocalDate.now(),
			10000L, List.of("contributor"), List.of("tag"), List.of(1L, 2L));
		ProductDocument productDocument = new ProductDocument(request);
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.of(productDocument));

		// when
		productSearchService.updateProductDocumentPublisher(productId, newName);

		// then
		verify(productSearchRepository, times(1)).save(any(ProductDocument.class));
	}

	@Test
	@DisplayName("update product document publisher - fail")
	void update_product_document_publisher_fail_test() {
		// given
		Long productId = 1L;
		String newName = "new publisher";
		when(productSearchRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productSearchService.updateProductDocumentPublisher(productId, newName))
			.isInstanceOf(ProductNotFoundException.class);
	}
}
