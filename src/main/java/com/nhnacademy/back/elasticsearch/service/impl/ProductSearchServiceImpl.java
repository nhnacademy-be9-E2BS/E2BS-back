package com.nhnacademy.back.elasticsearch.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.elasticsearch.domain.document.ProductDocument;
import com.nhnacademy.back.elasticsearch.domain.dto.request.RequestProductDocumentDTO;
import com.nhnacademy.back.elasticsearch.repository.CustomProductSearchRepository;
import com.nhnacademy.back.elasticsearch.repository.ProductSearchRepository;
import com.nhnacademy.back.elasticsearch.service.ProductSearchService;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

	private final ProductSearchRepository productSearchRepository;
	private final CustomProductSearchRepository customProductSearchRepository;

	@Override
	public void createProductDocument(RequestProductDocumentDTO request) {
		if (productSearchRepository.existsById(request.getProductId())) {
			throw new ProductAlreadyExistsException("Product already exists");
		}

		ProductDocument productDocument = new ProductDocument(request);
		productSearchRepository.save(productDocument);
	}

	@Override
	public Page<ResponseProductReadDTO> getProductsBySearch(Pageable pageable, String search, String sort) {
		return null;
	}

	@Override
	public Page<ResponseProductReadDTO> getProductsByCategoryId(Pageable pageable, Long categoryId, String sort) {
		return null;
	}

	@Override
	public void updateProductDocument(RequestProductDocumentDTO request) {
		ProductDocument productDocument = productSearchRepository.findById(request.getProductId())
			.orElseThrow(ProductNotFoundException::new);

		productDocument.updateProductDocument(request);
		productSearchRepository.save(productDocument);
	}

	@Override
	public void updateProductDocumentHits(Long productId) {
		ProductDocument productDocument = productSearchRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);

		productDocument.updateHits();
		productSearchRepository.save(productDocument);
	}

	@Override
	public void updateProductDocumentSearches(Long productId) {
		ProductDocument productDocument = productSearchRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);

		productDocument.updateSearches();
		productSearchRepository.save(productDocument);
	}

	@Override
	public void updateProductDocumentReview(Long productId, Integer reviewRate) {
		ProductDocument productDocument = productSearchRepository.findById(productId)
			.orElseThrow(ProductNotFoundException::new);

		Float currentReviewRate = productDocument.getProductReviewRate();
		Long currentReviewCount = productDocument.getProductReviewCount();
		if (currentReviewCount == 0) {
			productDocument.updateReview(Float.valueOf(reviewRate));
		} else {
			Float newReviewRate = (currentReviewRate * currentReviewCount + reviewRate) / (currentReviewCount + 1);
			productDocument.updateReview(newReviewRate);
		}
		productSearchRepository.save(productDocument);
	}
}
