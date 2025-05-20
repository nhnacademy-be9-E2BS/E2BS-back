package com.nhnacademy.back.product.category.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.product.category.domain.dto.request.RequestCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.exception.CategoryAlreadyExistsException;
import com.nhnacademy.back.product.category.exception.CategoryDeleteNotAllowedException;
import com.nhnacademy.back.product.category.exception.CategoryNotFoundException;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
	private final CategoryJpaRepository categoryJpaRepository;

	// 프록시 지연 주입
	@Lazy
	private final CategoryService self;

	public CategoryServiceImpl(CategoryJpaRepository categoryJpaRepository, @Lazy CategoryService self) {
		this.categoryJpaRepository = categoryJpaRepository;
		this.self = self;
	}

	/**
	 * 관리자 페이지 카테고리 탭 & 상품 등록 & 상품 수정 창에서 모든 Category를 볼 수 있도록 조회하는 로직
	 * 폴더 구조로 구현할 예정이기 때문에 페이징 처리 X
	 */
	@Cacheable(value = "Categories", key = "'all'")
	@Override
	public List<ResponseCategoryDTO> getCategories() {
		List<Category> allCategories = categoryJpaRepository.findAll();

		// categoryId 기준으로 Category 매핑
		Map<Long, ResponseCategoryDTO> dtoMap = new HashMap<>();

		// 최상위 카테고리 리스트
		List<ResponseCategoryDTO> rootCategories = new ArrayList<>();

		// 1차로 DTO 매핑
		for (Category category : allCategories) {
			dtoMap.put(category.getCategoryId(),
				new ResponseCategoryDTO(category.getCategoryId(), category.getCategoryName(), new ArrayList<>()));
		}

		// 2차로 부모-자식 연결
		for (Category category : allCategories) {
			ResponseCategoryDTO dto = dtoMap.get(category.getCategoryId());
			if (category.getParent() != null) {
				ResponseCategoryDTO parentDto = dtoMap.get(category.getParent().getCategoryId());
				parentDto.getChildren().add(dto);
			} else {
				rootCategories.add(dto); // 최상위 카테고리
			}
		}
		return rootCategories;
	}

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
		List<ResponseCategoryDTO> allCategories = self.getCategories();

		ResponseCategoryDTO targetCategory = findCategoryById(allCategories, categoryId);
		if (Objects.isNull(targetCategory)) {
			throw new CategoryNotFoundException();
		}

		return targetCategory.getChildren();
	}

	/**
	 * Categories라는 캐시 이름 안의 모든 키를 지우는 메소드
	 */
	@CacheEvict(value = "Categories", allEntries = true)
	@Override
	public void clearCategoriesCache() {
		// 필요한 경우 여기에 로그를 찍거나 추가 작업도 가능
		log.info("categories delete");
	}

	/**
	 * 관리자가 이미 존재하는 Category 하위에 다른 Category를 저장하는 로직
	 * 이름 : 동일한 단계(level) + 동일한 상위 카테고리 내에서 이름 중복 불가 -> 중복 되는 경우 Exception 발생
	 */
	@Transactional
	@Override
	public void createChildCategory(long parentId, RequestCategoryDTO request) {
		String categoryName = request.getCategoryName();

		Category parent = categoryJpaRepository.findById(parentId)
			.orElseThrow(CategoryNotFoundException::new);

		if (categoryJpaRepository.existsByParentCategoryIdAndCategoryName(parentId, categoryName)) {
			throw new CategoryAlreadyExistsException();
		}

		Category category = new Category(categoryName, parent);
		parent.getChildren().add(category); // 부모 카테고리에 자식 카테고리 추가
		categoryJpaRepository.save(category);

		self.clearCategoriesCache();
	}

	/**
	 * 관리자가 최상위 카테고리와 그 카테고리의 하위 카테고리를 저장하는 로직
	 * 이름 : 동일한 단계(level) + 동일한 상위 카테고리 내에서 이름 중복 불가 -> 중복 되는 경우 Exception 발생
	 */
	@Transactional
	@Override
	public void createCategoryTree(List<RequestCategoryDTO> request) {
		if (request.size() != 2) {
			throw new IllegalArgumentException();
		}

		// 최상위 카테고리 이름 중복 검사
		if (categoryJpaRepository.existsByParentIsNullAndCategoryName(request.get(0).getCategoryName())) {
			throw new CategoryAlreadyExistsException();
		}

		// 최상위 카테고리 저장
		Category parentCategory = categoryJpaRepository.save(
			new Category(request.get(0).getCategoryName(), null));

		// 하위 카테고리 저장
		Category childCategory = new Category(request.get(1).getCategoryName(), parentCategory);
		categoryJpaRepository.save(childCategory);

		self.clearCategoriesCache();
	}

	/**
	 * 관리자가 DB에 저장 되어 있는 Category의 값을 수정하는 로직
	 * 수정 가능한 값 : category_name, category_id2(parent)
	 * 수정 조건
	 * category_name : 동일한 단계(level) + 동일한 상위 카테고리 내에서 이름 중복 불가 -> 중복 되는 경우 Exception 발생
	 * category_id2 : 자식 카테고리가 없는 최하위 카테고리인 경우에만 이동 가능하며, 해당 카테고리에 속한 도서들도 같이 이동, 최상위 카테고리로 이동은 불가능
	 */
	@Transactional
	@Override
	public void updateCategory(long categoryId, RequestCategoryDTO request) {
		Category originCategory = categoryJpaRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);

		String newName = request.getCategoryName();

		if (categoryJpaRepository.existsByParentCategoryIdAndCategoryName(originCategory.getParent().getCategoryId(),
			newName)) {
			throw new CategoryAlreadyExistsException();
		}

		originCategory.setCategory(newName);
		categoryJpaRepository.save(originCategory);

		self.clearCategoriesCache();
	}

	/**
	 * 관리자가 DB에 저장 되어 있는 Category를 삭제하는 로직
	 * 삭제 조건 (모두 만족해야 함)
	 * 1. 자식 카테고리가 없는 최하위 카테고리
	 * 2. 2단계 카테고리라면 동일한 단계(level) + 동일한 상위 카테고리를 가지는 다른 카테고리가 존재 해야 함 (최소 2단계 요구 사항)
	 */
	@Transactional
	@Override
	public void deleteCategory(long categoryId) {
		Category category = categoryJpaRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);

		// 최하위 카테고리가 아닌 경우 삭제 불가능 (자식이 있으면 삭제 불가
		if (!category.getChildren().isEmpty()) {
			throw new CategoryDeleteNotAllowedException("하위 카테고리가 존재하여 삭제 불가");
		}

		Category parent = category.getParent();
		boolean isSecondLevel = parent.getParent() == null;

		if (isSecondLevel) {
			// 본인이 2단계 → 부모의 자식이 2개 이상이어야 삭제 가능 (최상위 카테고리 하나만 있는 경우 방지)
			if (parent.getChildren().size() < 2) {
				throw new CategoryDeleteNotAllowedException("최소 2단계로 구성해야 하므로 삭제 불가");
			}
		}

		// 3단계 이상 → 항상 삭제 가능
		parent.getChildren().remove(category);    // 먼저 부모 카테고리의 자식 카테고리 리스트에서 현재 카테고리 제거
		categoryJpaRepository.save(parent);
		categoryJpaRepository.deleteById(categoryId);

		self.clearCategoriesCache();
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
