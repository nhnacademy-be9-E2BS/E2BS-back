package com.nhnacademy.back.product.category.service;

import java.util.List;

import com.nhnacademy.back.product.category.domain.dto.request.RequestCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseSideBarCategoryDTO;

public interface CategoryService {

	void createCategory(long parentId, RequestCategoryDTO request);

	void createCategory(List<RequestCategoryDTO> requests);

	List<ResponseCategoryDTO> getCategories();

	List<ResponseCategoryDTO> getCategoriesToDepth3();

	List<ResponseSideBarCategoryDTO> getCategoriesById(long categoryId);

	void updateCategory(long categoryId, RequestCategoryDTO request);

	void deleteCategory(long categoryId);
}
