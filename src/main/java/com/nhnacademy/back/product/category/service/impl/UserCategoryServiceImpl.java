package com.nhnacademy.back.product.category.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.exception.CategoryNotFoundException;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.service.AdminCategoryService;
import com.nhnacademy.back.product.category.service.UserCategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCategoryServiceImpl implements UserCategoryService {
	private final CategoryJpaRepository categoryJpaRepository;
	private final AdminCategoryService adminCategoryService;

	/**
	 * html 헤더에서 보여줄 카테고리 리스트를 조회하는 로직 (depth 3단계 까지만)
	 * 캐시가 없는 경우 DB에서 캐싱하여 데이터를 저장
	 */
	@Cacheable(value = "Categories", key = "'header'")
	@Override
	public List<ResponseCategoryDTO> getCategoriesToDepth3() {
		List<Category> rootCategories = categoryJpaRepository.findAllByParentIsNull();

		return rootCategories.stream()
			.map(root -> buildTreeUpToDepth(root, 1))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	/**
	 * 사용자가 카테고리 선택 시 그에 해당하는 하위 카테고리들을 side bar에서 보여주기 위해
	 * categoryId의 하위 카테고리들을 조회하여 return 하는 로직
	 * ex) A-B-C-D-E에서 C를 누른 경우 side bar에서 D-E를 보여줌
	 */
	@Override
	public List<ResponseCategoryDTO> getCategoriesById(long categoryId) {
		List<ResponseCategoryDTO> allCategories = adminCategoryService.getCategories();

		ResponseCategoryDTO targetCategory = findCategoryById(allCategories, categoryId);
		if (Objects.isNull(targetCategory)) {
			throw new CategoryNotFoundException();
		}

		return targetCategory.getChildren();
	}

	/**
	 * getCategoriesToDepth3() 메소드에서 카테고리를 트리 구조로 만들기 위한 메소드
	 */
	private ResponseCategoryDTO buildTreeUpToDepth(Category category, int currentDepth) {
		if (currentDepth > 3) {
			return null;
		}

		List<ResponseCategoryDTO> childDtos = category.getChildren() == null ?
			new ArrayList<>() :
			category.getChildren().stream()
				.map(child -> buildTreeUpToDepth(child, currentDepth + 1))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		return new ResponseCategoryDTO(
			category.getCategoryId(),
			category.getCategoryName(),
			childDtos
		);
	}

	/**
	 * getCategoriesById(long categoryId) 메소드에서 해당 categoryId 노드를 찾기 위한 메소드
	 */
	private ResponseCategoryDTO findCategoryById(List<ResponseCategoryDTO> categories, long categoryId) {
		for (ResponseCategoryDTO category : categories) {
			if (category.getCategoryId() == categoryId) {
				return category;
			}

			ResponseCategoryDTO found = findCategoryById(category.getChildren(), categoryId);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

}
