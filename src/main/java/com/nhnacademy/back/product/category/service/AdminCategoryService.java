package com.nhnacademy.back.product.category.service;

import java.util.List;

import com.nhnacademy.back.product.category.domain.dto.request.RequestCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;

public interface AdminCategoryService {

	void createChildCategory(long parentId, RequestCategoryDTO request);

	void createCategoryTree(List<RequestCategoryDTO> requests);

	List<ResponseCategoryDTO> getCategories();

	void clearHeaderCategoriesCache();

	void updateCategory(long categoryId, RequestCategoryDTO request);

	void deleteCategory(long categoryId);
}
