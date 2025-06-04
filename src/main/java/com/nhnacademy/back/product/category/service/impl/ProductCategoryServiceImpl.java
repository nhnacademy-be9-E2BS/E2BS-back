package com.nhnacademy.back.product.category.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.category.domain.dto.ProductCategoryFlatDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryIdsDTO;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.category.service.ProductCategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductCategoryServiceImpl implements ProductCategoryService {
	private final ProductCategoryJpaRepository productCategoryJpaRepository;

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
			.toList();
	}
}
