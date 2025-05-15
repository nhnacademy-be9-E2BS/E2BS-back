package com.nhnacademy.back.product.product.kim.service;

import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;

public interface ProductService {
	void createProduct(RequestProductCreateDTO request);

	ResponseProductReadDTO readProductDetail(String productTitle);

	void updateProduct(RequestProductUpdateDTO request);

	void updateProductStock(RequestProductStockUpdateDTO request);
}
