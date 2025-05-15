package com.nhnacademy.back.product.category.service;

import java.util.List;

import com.nhnacademy.back.product.category.domain.dto.request.RequestModifyCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.request.RequestRegisterCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseSideBarCategoryDTO;

public interface CategoryService {

	void createCategory(long parentId, RequestRegisterCategoryDTO registerRequest);

	void createCategory(List<RequestRegisterCategoryDTO> registerRequests);

	List<ResponseCategoryDTO> getCategories();

	List<ResponseCategoryDTO> getCategoriesToDepth3();

	List<ResponseSideBarCategoryDTO> getCategoriesById(long categoryId);

	void updateCategory(long categoryId, RequestModifyCategoryDTO modifyRequest);

	void deleteCategory(long categoryId);
}
