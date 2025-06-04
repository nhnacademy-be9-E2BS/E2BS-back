package com.nhnacademy.back.product.category.service;

import java.util.List;

import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryIdsDTO;

public interface ProductCategoryService {

	List<ResponseCategoryIdsDTO> getCategoriesByProductId(List<Long> productIds);

}
