package com.nhnacademy.back.product.category.service;

import java.util.List;

import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;

public interface UserCategoryService {

	List<ResponseCategoryDTO> getCategoriesToDepth3();

	List<ResponseCategoryDTO> getCategoriesById(long categoryId);

}
