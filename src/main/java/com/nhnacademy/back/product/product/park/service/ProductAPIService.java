package com.nhnacademy.back.product.product.park.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiSearchDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductsApiSearchDTO;

public interface ProductAPIService {
	Page<ResponseProductsApiSearchDTO> searchProducts(RequestProductApiSearchDTO request, Pageable pageable); //api에서 책 리스트 가져오기
	void createProduct(RequestProductApiCreateDTO request);

}