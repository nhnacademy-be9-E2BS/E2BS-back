package com.nhnacademy.back.product.category.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nhnacademy.back.product.category.domain.dto.ProductCategoryFlatDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryIdsDTO;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;
import com.nhnacademy.back.product.category.exception.CategoryNotFoundException;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.category.service.ProductCategoryService;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {
	private final ProductCategoryJpaRepository productCategoryJpaRepository;
	private final ProductJpaRepository productJpaRepository;
	private final CategoryJpaRepository categoryJpaRepository;

	/**
	 * 상품 등록 & 수정 시 상품카테고리 관계를 추가하는 로직
	 * isUpdate = true는 상품 수정
	 * isUpdate = false는 상품 등록
	 * (선택한 카테고리의 상위 카테고리들도 전부 저장)
	 * (productIds는 선택한 카테고리)
	 */
	@Override
	public void createProductCategory(long productId, List<Long> categoryIds, boolean isUpdate) {
		if (isUpdate) {
			productCategoryJpaRepository.deleteAllByProductId(productId);
		}

		Set<Long> uniqueCategoryIds = new HashSet<>();

		for (Long categoryId : categoryIds) {
			Category category = categoryJpaRepository.findById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException("Category Not Found, id: " + categoryId));

			while (category != null) {
				uniqueCategoryIds.add(category.getCategoryId());
				category = category.getParent();
			}
		}

		Product product = productJpaRepository.findById(productId)
			.orElseThrow(() -> new ProductNotFoundException("Product Not Found, id: " + productId));

		for (Long id : uniqueCategoryIds) {
			Category category = categoryJpaRepository.findById(id)
				.orElseThrow(() -> new CategoryNotFoundException("Category Not Found, id: " + id));
			productCategoryJpaRepository.save(new ProductCategory(product, category));
		}
	}

	/**
	 * 상품 ID 리스트를 받아서 각 상품의 카테고리 ID 리스트를 반환해주는 로직
	 */
	@Override
	public List<ResponseCategoryIdsDTO> getCategoriesByProductId(List<Long> productIds) {
		List<ProductCategoryFlatDTO> flatResults = productCategoryJpaRepository.findFlatCategoryData(productIds);

		Map<Long, List<Long>> grouped = flatResults.stream()
			.collect(Collectors.groupingBy(
				ProductCategoryFlatDTO::getProductId,
				Collectors.mapping(ProductCategoryFlatDTO::getCategoryId, Collectors.toList())
			));

		return grouped.entrySet().stream()
			.map(entry -> new ResponseCategoryIdsDTO(entry.getKey(), entry.getValue()))
			.collect(Collectors.toList());
	}
}
