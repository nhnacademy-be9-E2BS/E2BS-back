package com.nhnacademy.back.product.product.park.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateByQueryDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiSearchDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiSearchByQueryTypeDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductsApiSearchDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductApiSearchByQueryTypeDTO;

public interface ProductAPIService {
	Page<ResponseProductsApiSearchDTO> searchProducts(RequestProductApiSearchDTO request, Pageable pageable); //api에서 책 리스트 가져오기
	void createProduct(RequestProductApiCreateDTO request);

	void createProductByQuery(RequestProductApiCreateByQueryDTO request);


	Page<ResponseProductApiSearchByQueryTypeDTO> searchProductsByQuery(RequestProductApiSearchByQueryTypeDTO request, Pageable pageable);

}