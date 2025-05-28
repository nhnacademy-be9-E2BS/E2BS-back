package com.nhnacademy.back.product.category.service;

import java.util.List;

import com.nhnacademy.back.product.category.domain.dto.request.RequestCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;

public interface CategoryService {

	List<ResponseCategoryDTO> getCategories();

	List<ResponseCategoryDTO> getCategoriesToDepth3();

	void clearCategoriesCache();

	void createChildCategory(long parentId, RequestCategoryDTO request);

	void createCategoryTree(List<RequestCategoryDTO> requests);

	void updateCategory(long categoryId, RequestCategoryDTO request);

	void deleteCategory(long categoryId);
}
