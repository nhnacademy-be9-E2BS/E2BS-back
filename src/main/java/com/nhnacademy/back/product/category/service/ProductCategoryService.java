package com.nhnacademy.back.product.category.service;

import java.util.List;

import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryIdsDTO;

public interface ProductCategoryService {

	void createProductCategory(long productId, List<Long> categoryIds, boolean isUpdate);

	List<ResponseCategoryIdsDTO> getCategoriesByProductId(List<Long> productIds);

}
